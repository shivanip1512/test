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
#include "ccoriginalparent.h"
#include "ccid.h"
#include "logger.h"
#include "utility.h"
                     
#include <rw/collstr.h>
using namespace std;

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

    case CtiCCCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS:
        ReturnFeederToOriginalSubBus();
        break;
    case CtiCCCommand::RESET_DAILY_OPERATIONS:
        ResetDailyOperations();
        break;

    case CtiCCCommand::CONFIRM_FEEDER:
        ConfirmFeeder();
        break;

    case CtiCCCommand::RESET_SYSTEM_OP_COUNTS:
        ResetAllSystemOpCounts();
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
    case CtiCCCommand::SEND_TIME_SYNC:
        SendTimeSync(); 
        break;
    case CtiCCCommand::CHANGE_OPERATIONALSTATE:
        changeBankOperationalState(); 
        break; 
    case CtiCCCommand::AUTO_ENABLE_OVUV:
        AutoEnableOvUv(); 
        break; 
    case CtiCCCommand::AUTO_DISABLE_OVUV:
        AutoDisableOvUv(); 
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
    CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(subID);
    CtiCCAreaPtr currentArea = NULL;
    CtiCCSubstationPtr currentStation = NULL;
    if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag() && 
        currentSubstationBus->getStrategyId() > 0) 
    {
        currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
        if (currentStation != NULL && !currentStation->getDisableFlag())
        {   
            currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
            if (currentArea != NULL && !currentArea->getDisableFlag()) 
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
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));
                                
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
                    currentSubstationBus->setVerificationDisableOvUvFlag(_subVerificationMsg->getDisableOvUvFlag());
                    currentSubstationBus->setCapBankInactivityTime(_subVerificationMsg->getInactivityTime());
                    currentSubstationBus->setBusUpdatedFlag(TRUE);

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
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));

                    CtiCCExecutorFactory f;
                    CtiCCExecutor* executor;
                    if (currentSubstationBus->getVerificationDisableOvUvFlag())
                    {
                        executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentSubstationBus->getPAOId()));
                        executor->Execute();
                        delete executor;
                    }
                    CtiCCCommand* cmdMsg = new CtiCCCommand(CtiCCCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPAOId());
                    cmdMsg->setUser(_subVerificationMsg->getUser());
                    executor = f.createExecutor(cmdMsg);
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
                        currentSubstationBus->setVerificationDisableOvUvFlag(_subVerificationMsg->getDisableOvUvFlag());
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
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));
                    }
                }
            }
            else
            {
                if( currentArea != NULL &&
                    _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() <<  " - Verification Not Enabled on SubBus: "<<currentSubstationBus->getPAOName() 
                    <<" due to Area: "<<currentArea->getPAOName() <<" DisableFlag"<< endl;
                }
            }
        }
        else
        {
            if( currentStation != NULL &&
                _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() <<  " - Verification Not Enabled on SubBus: "<<currentSubstationBus->getPAOName() 
                <<" due to Substation: "<<currentStation->getPAOName() <<" DisableFlag"<< endl;
            }
        }
    }
    else
    {
        if( currentSubstationBus != NULL &&
            _CC_DEBUG & CC_DEBUG_STANDARD )
        {

            if (currentSubstationBus->getDisableFlag())
            {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() <<  " - Verification Not Enabled on SubBus: "<<currentSubstationBus->getPAOName() <<" due to SubBus DisableFlag"<< endl;
        }
            else
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() <<  " - Verification Not Enabled on SubBus: "<<currentSubstationBus->getPAOName() <<" due to SubBus Strategy = (none)"<< endl;
            }

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

                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text, "cap control"));

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

                if (currentSubstationBus->getVerificationDisableOvUvFlag())
                {
                    CtiCCExecutorFactory f;
                    CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentSubstationBus->getPAOId()));
                    executor->Execute();
                    delete executor;
                    currentSubstationBus->setVerificationDisableOvUvFlag(FALSE);
                }

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

                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlDisableVerification, currentSubstationBus->getEventSequence(), 0, text, "cap control"));
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

            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
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

            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
            
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
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
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

                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
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

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
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

                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
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
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getControlPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));

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
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - ControlDeviceID: "<<controlID<<" - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
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
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getControlPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));

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
    }
    else
    {
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - ControlDeviceID: "<<controlID<<" - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }

}

void CtiCCCommandExecutor::changeBankOperationalState()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    long paoId = _command->getId();
    CtiCCCapBankPtr capBankPtr = store->findCapBankByPAObjectID(paoId);
    if( capBankPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Capbank does not exist, cannot change operational state." << endl;
        return;
    }
    capBankPtr->setOperationalState( _command->getToken() );
    store->UpdateCapBankOperationalStateInDB( capBankPtr );

    CtiSignalMsg* msg = new CtiSignalMsg(SYS_PID_CAPCONTROL, 0, 
                                         "Cap Bank Update", 
                                         "Manual Op State Changed: " + capBankPtr->getOperationalState() + " for Bank: " + capBankPtr->getPAOName(),
                                         CapControlLogType );
    msg->setUser( _command->getUser() );
    msg->setMessageTime( CtiTime() );

    CtiCapController::getInstance()->sendMessageToDispatch( msg );
    CtiCCFeederPtr feederPtr = store->findFeederByPAObjectID( capBankPtr->getParentId() );
    if( feederPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Changed operational state of an orphaned Cap Bank." << endl;
        return;
    }
    CtiCCSubstationBusPtr subbusPtr = store->findSubBusByPAObjectID( feederPtr->getParentId() );
    if( subbusPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Changed operational state of an orphaned Cap Bank." << endl;
        return;
    }
    subbusPtr->setBusUpdatedFlag(true);
}

void CtiCCCommandExecutor::SendTimeSync()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG paoId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(paoId);
    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(paoId);
    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(paoId);
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(paoId);
    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(paoId);
    CtiCCCapBankPtr currentCapBank = store->findCapBankByPAObjectID(paoId);
    if (currentCapBank != NULL) 
    {
        controlID = currentCapBank->getControlDeviceId();
        if (controlID > 0)
        {   
            pilMessages.push_back(new CtiRequestMsg(controlID,"putconfig timesync"));
        }
    }
    else if (currentFeeder != NULL)
    {
        currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

        if (currentSubstationBus != NULL)
        {
            string text1 = string("Feeder: ");
            text1 += currentFeeder->getPAOName();
            text1 += string(" TimeSync All CapBanks");
            string additional1 = string("Feeder: ");
            additional1 += currentFeeder->getPAOName();
            
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
            
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            
            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                {
                    controlID = currentCapBank->getControlDeviceId();
                    if (controlID > 0 &&
                        stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702"))
                    {   
                        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig timesync");
                        reqMsg->setUser(_command->getUser());
                        pilMessages.push_back(reqMsg);
                    }
                }
            }
            //modifiedSubsList.push_back(currentSubstationBus);
        }
    }
    else if (currentSubstationBus != NULL)
    {
        string text1 = string("SubBus: ");
        text1 += currentSubstationBus->getPAOName();
        text1 += string(" TimeSync All CapBanks");
        string additional1 = string("SubBus: ");
        additional1 += currentSubstationBus->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
        
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();


            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                {
                    controlID = currentCapBank->getControlDeviceId();
                    if (controlID > 0 &&
                        stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702"))
                    {   
                        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig timesync");
                        reqMsg->setUser(_command->getUser());
                        pilMessages.push_back(reqMsg);
                    }
                }
            }
        }
        //modifiedSubsList.push_back(currentSubstationBus);

    }
    else if (currentStation != NULL)
    {

        string text1 = string("Substation: ");
        text1 += currentStation->getPAOName();
        text1 += string(" TimeSync All CapBanks");
        string additional1 = string("Substation: ");
        additional1 += currentStation->getPAOName();
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));

        list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
        while (iterBus  != currentStation->getCCSubIds()->end())
        { 
            LONG busId = *iterBus;
            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL)
            {
                currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
               
                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
               
               
                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                        {
                            controlID = currentCapBank->getControlDeviceId();
                            if (controlID > 0 && 
                                stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702"))
                            {   
                                CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig timesync");
                                reqMsg->setUser(_command->getUser());
                                pilMessages.push_back(reqMsg);
                            }
                        }
                    }
                }
                //modifiedSubsList.push_back(currentSubstationBus);
            }
            iterBus++;
        }
    }
    else if (currentArea != NULL)
    {
        
        string text1 = string("Area: ");
        text1 += currentArea->getPAOName();
        text1 += string(" TimeSync All CapBanks");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
       
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        std::list <long>::iterator subIter = currentArea->getSubStationList()->begin();
       
        while (subIter != currentArea->getSubStationList()->end())
        {
            currentStation = store->findSubstationByPAObjectID(*subIter);
            subIter++;
       
            if (currentStation != NULL)
            {
                list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
                while (iterBus  != currentStation->getCCSubIds()->end())
                { 
                    LONG busId = *iterBus;
                    CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL)
                    {
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                        
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                       
                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                       
                       
                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                                {
                                    controlID = currentCapBank->getControlDeviceId();
                                    if (controlID > 0 &&
                                        stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702"))
                                    {   
                                        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig timesync");
                                        reqMsg->setUser(_command->getUser());
                                        pilMessages.push_back(reqMsg);
                                    }
                                }
                            }
                        }
                        //modifiedSubsList.push_back(currentSubstationBus);
                    }
                    iterBus++;
                }
            }
        }
    }
    else if (currentSpArea != NULL)
    {
        if(currentSpArea->getDisableFlag())
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Special Area is not enabled, commands refused." << endl;
            }

            CtiCCExecutorFactory f;
            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::COMMAND_REFUSED, "Special Area is not enabled.");
            msg->setUser(_command->getUser());
            CtiCCExecutor* executor = f.createExecutor(msg);
            executor->Execute();
            delete executor;
        }
        else
        {
            string text1 = string("Special Area: ");
            text1 += currentSpArea->getPAOName();
            text1 += string(" TimeSync All CapBanks");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPAOName();
           
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            std::list <long>::iterator subIter = currentSpArea->getSubstationIds()->begin();
           
            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                currentStation = store->findSubstationByPAObjectID(*subIter);
                subIter++;
           
                if (currentStation != NULL)
                {
                    list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
                    while (iterBus  != currentStation->getCCSubIds()->end())
                    { 
                        LONG busId = *iterBus;
                        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                        if (currentSubstationBus != NULL)
                        {
                            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                            
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                           
                            for(LONG j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                           
                           
                                for(LONG k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                                    {
                                        controlID = currentCapBank->getControlDeviceId();
                                        if (controlID > 0 &&
                                            stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702"))
                                        {   
                                            CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig timesync");
                                            reqMsg->setUser(_command->getUser());
                                            pilMessages.push_back(reqMsg);
                                        }
                                    }
                                }
                            }
                            //modifiedSubsList.push_back(currentSubstationBus);
                        }
                        iterBus++;
                    }
                }
            }
        }
    }
    else
    {
        //nothing.
    }

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0 )
    {
        CtiCapController::getInstance()->confirmCapBankControl( multiPilMsg, multi );
    }
    else
    {
        delete multiPilMsg;
        delete multi;
    }

    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    return;

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
            ControlAllCapBanks(paoId, CtiCCCapBank::Open);
            break;
        }
        case CtiCCCommand::SEND_ALL_CLOSE:
        {
            actionText = string(" - Close");
            action = CtiCCCommand::CLOSE_CAPBANK;
            ControlAllCapBanks(paoId, CtiCCCapBank::Close);
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
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    if (action  != CtiCCCommand::OPEN_CAPBANK &&
        action  != CtiCCCommand::CLOSE_CAPBANK )
    {
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
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                                
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

                                        if (action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK)
                                        {
                                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                            currentFeeder->setRecentlyControlledFlag(TRUE);
                                            currentCapBank->setSendAllCommandFlag(TRUE);

                                        }
                                        
                                        CtiCCCommand* actionMsg = new CtiCCCommand(action, currentCapBank->getControlDeviceId());
                                        actionMsg->setUser(_command->getUser());
                                        actionMulti->insert(actionMsg);
                                        

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
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                           
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
                                            if (action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK)
                                            {
                                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                                currentFeeder->setRecentlyControlledFlag(TRUE);
                                                currentCapBank->setSendAllCommandFlag(TRUE);
                                            }

                                            CtiCCCommand* actionMsg = new CtiCCCommand(action, currentCapBank->getControlDeviceId());
                                            actionMsg->setUser(_command->getUser());
                                            actionMulti->insert(actionMsg);

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
        if (currentStation == NULL) 
        {
            currentStation = store->findSubstationByPAObjectID(paoId);
            if ( currentStation != NULL)
            {   
                currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                if (currentArea != NULL) 
                {
                    string text1 = string("Substation: ");
                    text1 += currentStation->getPAOName();
                    text1 += actionText;
                    text1 += string(" All CapBanks");
                    string additional1 = string("Substation: ");
                    additional1 += currentStation->getPAOName();
                   
                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                        currentStation->setOvUvDisabledFlag(FALSE);
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                        currentStation->setOvUvDisabledFlag(TRUE);
                    
                    list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
                    while (iterBus  != currentStation->getCCSubIds()->end())
                    { 
                        LONG busId = *iterBus;
                        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                        if (currentSubstationBus != NULL)
                        {
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                currentSubstationBus->setOvUvDisabledFlag(FALSE);
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                currentSubstationBus->setOvUvDisabledFlag(TRUE);
                            if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                   !currentSubstationBus->getDisableFlag() )  ||
                                   (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                    action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                            {
                                currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                               
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

                                                if (action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK)
                                                {
                                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                                    currentFeeder->setRecentlyControlledFlag(TRUE);
                                                    currentCapBank->setSendAllCommandFlag(TRUE);
                                                }

                                                CtiCCCommand* actionMsg = new CtiCCCommand(action, currentCapBank->getControlDeviceId());
                                                actionMsg->setUser(_command->getUser());
                                                actionMulti->insert(actionMsg);
                                            }
                                        }
                                    }
                                }
                       
                                modifiedSubsList.push_back(currentSubstationBus);
                            }
                        }
                        iterBus++;
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
                        if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                            currentStation->setOvUvDisabledFlag(FALSE);
                        if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                            currentStation->setOvUvDisabledFlag(TRUE);

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
                                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                                   
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
                                                    if (action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK)
                                                    {
                                                        currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                                        currentFeeder->setRecentlyControlledFlag(TRUE);
                                                        currentCapBank->setSendAllCommandFlag(TRUE);
                                                    }

                                                    CtiCCCommand* actionMsg = new CtiCCCommand(action, currentCapBank->getControlDeviceId());
                                                    actionMsg->setUser(_command->getUser());
                                                    actionMulti->insert(actionMsg);

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
                        if(currentSpArea->getDisableFlag())
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Special Area is not enabled, commands refused." << endl;
                            }
                
                            CtiCCExecutorFactory f;
                            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::COMMAND_REFUSED, "Special Area is not enabled.");
                            msg->setUser(_command->getUser());
                            CtiCCExecutor* executor = f.createExecutor(msg);
                            executor->Execute();
                            delete executor;
                        }
                        else
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
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                        currentStation->setOvUvDisabledFlag(FALSE);
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                        currentStation->setOvUvDisabledFlag(TRUE);
        
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
                                                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
        
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
        
                                                                if (action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK)
                                                                {
                                                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                                                    currentFeeder->setRecentlyControlledFlag(TRUE);
                                                                    currentCapBank->setSendAllCommandFlag(TRUE);
                                                                }
                                                                CtiCCCommand* actionMsg = new CtiCCCommand(action, currentCapBank->getControlDeviceId());
                                                                actionMsg->setUser(_command->getUser());
                                                                actionMulti->insert(actionMsg);
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

        }
    }

    if (actionMulti->getCount() > 0)
    {
       CtiCCExecutorFactory f;
       CtiCCExecutor* executor = f.createExecutor(actionMulti);
       executor->Execute();
       delete executor;
    }
    else
    {
        delete actionMulti;
    } 
    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas));
    executor->Execute();
    delete executor;

    executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
    executor->Execute();
    delete executor;

    executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
    executor->Execute();
    delete executor;

    executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
    executor->Execute();
    delete executor;

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;

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

bool CtiCCCommandExecutor::checkForCommandRefusal(CtiCCFeeder* feeder)
{
    CtiCCCapBank_SVector& ccCapBanks = feeder->getCCCapBanks();

    for(LONG k=0;k<ccCapBanks.size();k++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
        int status = currentCapBank->getControlStatus();
        if (status == CtiCCCapBank::OpenPending || status == CtiCCCapBank::ClosePending)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder already has a bank in a pending state." << endl;
            }

            CtiCCExecutorFactory f;
            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::COMMAND_REFUSED, "Another bank is already in a Pending State.");
            msg->setUser(_command->getUser());
            CtiCCExecutor* executor = f.createExecutor(msg);
            executor->Execute();
            delete executor;

            return true;
        }
    }

    return false;
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

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;


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
                        if(!currentCapBank->getSendAllCommandFlag() && 
                           checkForCommandRefusal(currentFeeder))
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Open Capbank Command Refused" << endl;
                            }
                            break;
                        }
                        if (!currentCapBank->getSendAllCommandFlag())
                            updatedSubs.push_back(currentSubstationBus);
                        
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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
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

                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

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
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 
                                                                        currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), 
                                                                        currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, 
                                                                        currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));

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

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            BOOL confirmImmediately = FALSE;
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::TimeOfDayMethod)||
                                currentCapBank->getSendAllCommandFlag() )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
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
                                currentCapBank->setSendAllCommandFlag(FALSE);
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
                            }
                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag()) 
                            {    
                                parentStation->setRecentlyControlledFlag(TRUE);
                                updatedStations.push_back(parentStation);
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
        else
            delete eventMulti;
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs));
            executor->Execute();
            delete executor;
        }
        if (updatedStations.size() > 0)
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationsMsg(updatedStations));
            executor->Execute();
            delete executor;
        }
    }
    else
    {
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
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

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;

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

                        if(!currentCapBank->getSendAllCommandFlag() && 
                           checkForCommandRefusal(currentFeeder))
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Close Capbank Command Refused" << endl;
                            }
                            break;
                        }
                        if (!currentCapBank->getSendAllCommandFlag())
                            updatedSubs.push_back(currentSubstationBus);

                        currentCapBank->setSendAllCommandFlag(FALSE);


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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
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
                                
                                BOOL confirmImmediately = FALSE;
                                if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::TimeOfDayMethod) ||
                                    currentCapBank->getSendAllCommandFlag())
                                {
                                    confirmImmediately = TRUE;
                                }
                                else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
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

                                if (confirmImmediately)
                                {
                                    doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
                                    currentCapBank->setSendAllCommandFlag(FALSE);

                                }
                                else
                                {

                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    currentFeeder->setRecentlyControlledFlag(TRUE);
                                    
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
                                        }
                                        else
                                        {
                                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                        }
                                    
                                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                        currentCapBank->setLastStatusChangeTime(CtiTime());
                                    }
                                    currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                    currentCapBank->setAfterVarsString(" --- ");
                                    currentCapBank->setPercentChangeString(" --- ");
                                  
                                  
                                    INT seqId = CCEventSeqIdGen();
                                    currentSubstationBus->setEventSequence(seqId);
                                    currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                    string stateInfo = currentCapBank->getControlStatusQualityString();
                                    LONG stationId, areaId, spAreaId;
                                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                    ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));
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
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            
                            }
                            

                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag()) 
                            {    
                                parentStation->setRecentlyControlledFlag(TRUE);
                                updatedStations.push_back(parentStation);
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
        if (updatedStations.size() > 0)
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationsMsg(updatedStations));
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

void CtiCCCommandExecutor::ControlAllCapBanks(LONG paoId, int control)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiCCSubstationBus_vec updatedSubs;
    
    CtiCCSpecialPtr spArea = store->findSpecialAreaByPAObjectID(paoId);
    CtiCCAreaPtr area = store->findAreaByPAObjectID(paoId);
    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(paoId);
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(paoId);
    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(paoId);

    if (currentFeeder != NULL)
    {
        if (!currentFeeder->getDisableFlag())
        {       

            ControlAllCapBanksByFeeder(currentFeeder->getPAOId(), control,
                                   pilMessages, pointChanges, ccEvents);
        }
    }
    else if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
    {

        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if (!currentFeeder->getDisableFlag())
            {
                ControlAllCapBanksByFeeder(currentFeeder->getPAOId(), control,
                                       pilMessages, pointChanges, ccEvents);
            }
        }
    }
    else if (station != NULL && !station->getDisableFlag())
    {
        list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
        while (iterBus  != station->getCCSubIds()->end())
        { 
            LONG busId = *iterBus;
            iterBus++;
            currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
            { 
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    if (!currentFeeder->getDisableFlag() )
                    {
                        ControlAllCapBanksByFeeder(currentFeeder->getPAOId(), control,
                                               pilMessages, pointChanges, ccEvents);
                    }
                }
            }
        }
    }
    else if (area != NULL && !area->getDisableFlag())
    {
        std::list <long>::iterator subIter = area->getSubStationList()->begin();

        while (subIter != area->getSubStationList()->end())
        {
            station = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (station != NULL && !station->getDisableFlag())
            {
                list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                { 
                    LONG busId = *iterBus;
                    iterBus++;
                    currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
                    { 
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
               
                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                            if (!currentFeeder->getDisableFlag() )
                            {
                                ControlAllCapBanksByFeeder(currentFeeder->getPAOId(), control,
                                                       pilMessages, pointChanges, ccEvents);
                            }
                        }
                    }
                }  
            }
        }

    }
    else if (spArea != NULL && !spArea->getDisableFlag())
    {
        std::list <long>::iterator subIter = spArea->getSubstationIds()->begin();

        while (subIter != spArea->getSubstationIds()->end())
        {
            station = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (station != NULL && !station->getDisableFlag())
            {
                list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                { 
                    LONG busId = *iterBus;
                    iterBus++;
                    currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
                    { 
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
               
                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                            if (!currentFeeder->getDisableFlag() )
                            {
                                ControlAllCapBanksByFeeder(currentFeeder->getPAOId(), control,
                                                       pilMessages, pointChanges, ccEvents);
                            }
                        }
                    }
                }  
            }
        }

    }

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;

    if (updatedSubs.size() > 0) 
    {
        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs));
        executor->Execute();
        delete executor;
    }
}

                                    
void CtiCCCommandExecutor::ControlAllCapBanksByFeeder(LONG feederId, int control, CtiMultiMsg_vec& pilMessages,
                                                      CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    
    BOOL found = FALSE;

    CtiCCSubstationBus_vec updatedSubs;

    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(feederId);
    if (currentFeeder != NULL)
    {
        CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus != NULL)
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            
            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( !currentCapBank->getDisableFlag() &&
                    currentCapBank->getControlDeviceId() > 0 &&
                    ( !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ||
                      !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::StandAloneState)) )
                    
                {
                    updatedSubs.push_back(currentSubstationBus);
                    if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                    {    
                        controlID = currentCapBank->getControlDeviceId();
                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
                            currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                        currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
                        if (control == CtiCCCapBank::Close || control == CtiCCCapBank::CloseQuestionable || 
                            control == CtiCCCapBank::CloseFail || control == CtiCCCapBank::ClosePending )
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                                   currentCapBank, TRUE);
                        if (control == CtiCCCapBank::Open || control == CtiCCCapBank::OpenQuestionable ||
                            control == CtiCCCapBank::OpenFail || control == CtiCCCapBank::OpenPending )
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                                   currentCapBank, TRUE);
           
                        currentCapBank->setControlStatusQuality(CC_Normal);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
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
                            string text = "Manual ";
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                text += "Open Sent, Sub VarLoad = ";
                            else
                                text += "Close Sent, Sub VarLoad = ";
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
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                            if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                            {
                                kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            }
                            else
                            {
                                kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            }

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(CtiTime());
                            
                            currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                            currentCapBank->setAfterVarsString(" --- ");
                            currentCapBank->setPercentChangeString(" --- ");
                            
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                            string stateInfo = currentCapBank->getControlStatusQualityString();
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));
                            
                            if( controlID > 0 )
                            {
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending) 
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
                            doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);
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
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
            
                        }
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
                }
            }
            currentSubstationBus->verifyControlledStatusFlags();
        }
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
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        currentArea->setDisableFlag(FALSE);
        store->UpdateAreaDisableFlagInDB(currentArea);

               
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        
        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
        executor->Execute();
        delete executor;
    }
    else
    {
        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //CHECK ALL SUBSTATIONS ASSIGNED TO SPECIAL AREA FIRST, 
            //To make sure they are not already on a conflicting different enabled Special Area
            BOOL refusalFlag = FALSE;
            string refusalText = "Special Area is not enabled!";
            std::list <long>::iterator subIter = currentSpArea->getSubstationIds()->begin();
            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(*subIter);
                subIter++;            
                if (currentSubstation != NULL)
                {
                    if (currentSubstation->getSaEnabledFlag()) 
                    {      
                        refusalFlag = TRUE;
                        CtiCCSpecial* enabledSpArea = store->findSpecialAreaByPAObjectID(currentSubstation->getSaEnabledId());
                        if (enabledSpArea != NULL)
                        {
                            refusalText +=  "  Special Area: " + enabledSpArea->getPAOName() + " with Sub: " + currentSubstation->getPAOName() + " is already ENABLED";
                        }
                        
                        

                    }
                }
            }

            if (refusalFlag)
            {
                CtiCCExecutorFactory f;
                CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::COMMAND_REFUSED, refusalText);
                msg->setUser(_command->getUser());
                CtiCCExecutor* executor = f.createExecutor(msg);
                executor->Execute();
                delete executor;
            }
            else
            {

                string text1 = string("Manual Enable Special Area");
                string additional1 = string("Special Area: ");
                additional1 += currentSpArea->getPAOName();
                
                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));
                
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
                        if (!currentSubstation->getSaEnabledFlag()) 
                        {
                            currentSubstation->setSaEnabledFlag(TRUE);
                            currentSubstation->setSaEnabledId(areaId);
                        }
                    }
                }
                //store->setValid(false);  //This is to do a full DATABASE RELOAD.
                
                if (eventMulti->getCount() > 0)
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                else
                    delete eventMulti;
                if (multi->getCount() > 0)
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                else
                    delete multi;
                
                CtiCCExecutorFactory f;
                CtiCCExecutor *executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
                executor->Execute();
                delete executor;
            }
        }
        else
        {
            CtiCCSubstation* station = store->findSubstationByPAObjectID(areaId);
            if (station != NULL)
            {
                string text1 = string("Manual Enable Substation");
                string additional1 = string("Substation: ");
                additional1 += station->getPAOName();
                
                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, station->getPAOId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                station->setDisableFlag(FALSE);
                store->UpdateSubstationDisableFlagInDB(station);
              
                       
                if (eventMulti->getCount() > 0)
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                else
                    delete eventMulti;
                if (multi->getCount() > 0)
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                else
                    delete multi;
              
                
                CtiCCExecutorFactory f;
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds()))); 
                executor->Execute();
                delete executor;            

            }
        }
    }
}

/*---------------------------------------------------------------------------
    DisableArea
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
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Disable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL,0, currentArea->getPAOId(), 0,  0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        currentArea->setDisableFlag(TRUE);
        store->UpdateAreaDisableFlagInDB(currentArea);
        currentArea->checkForAndStopVerificationOnChildSubBuses(capMessages);
        
        
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;


        CtiMultiMsg_vec& temp = multiCapMsg->getData( );
        for(int i=0;i<temp.size( );i++)
        {
        
            CtiCCExecutorFactory f;
           CtiCCExecutor* executor = f.createExecutor((CtiCCMessage*)capMessages[i]);
            executor->Execute();
            delete executor;
        
        }

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
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

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
                    if (currentSubstation->getSaEnabledFlag() && 
                        currentSubstation->getSaEnabledId() == areaId) 
                    {
                        currentSubstation->setSaEnabledFlag(FALSE);
                    }
                }
            }
            //store->setValid(false);  //This is to do a full DATABASE RELOAD.


            if (eventMulti->getCount() > 0)
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            else
                delete eventMulti;

            if (multi->getCount() > 0)
                CtiCapController::getInstance()->sendMessageToDispatch(multi);
            else
                delete multi;


            CtiCCExecutorFactory f;
            CtiCCExecutor *executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
            executor->Execute();
            delete executor;

        }
        else
        {
            CtiCCSubstation* station = store->findSubstationByPAObjectID(areaId);
            if (station != NULL)
            {
                string text1 = string("Manual Disable Substation");
                string additional1 = string("Substation: ");
                additional1 += station->getPAOName();
                
                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0,0, station->getPAOId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                station->setDisableFlag(TRUE);
                store->UpdateSubstationDisableFlagInDB(station);
                station->checkForAndStopVerificationOnChildSubBuses(capMessages);
                       
                if (eventMulti->getCount() > 0)
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                else
                    delete eventMulti;
                if (multi->getCount() > 0)
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                else
                    delete multi;
              
                
                CtiMultiMsg_vec& temp = multiCapMsg->getData( );
                for(int i=0;i<temp.size( );i++)
                {

                    CtiCCExecutorFactory f;
                    CtiCCExecutor* executor = f.createExecutor((CtiCCMessage*)capMessages[i]);
                    executor->Execute();
                    delete executor;
                }

                
                CtiCCExecutorFactory f;
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds()))); 
                executor->Execute();
                delete executor;            

            }
        }
    }

}


void CtiCCCommandExecutor::AutoEnableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.                              

    LONG cmdId = _command->getId();

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
    if (currentArea != NULL || currentSpArea != NULL)
    {   
        AutoEnableOvUvByArea();
        return;
    }
    if (currentStation != NULL)
    {
        AutoControlOvUvBySubstation(FALSE);
        return;
    }
    if (currentSubBus != NULL)
    {
        AutoControlOvUvBySubBus(FALSE);
        return;
    }
    return;
}

void CtiCCCommandExecutor::AutoDisableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.                              

    LONG cmdId = _command->getId();

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
    if (currentArea != NULL || currentSpArea != NULL)
    {   
        AutoDisableOvUvByArea();
        return;
    }
    if (currentStation != NULL)
    {
        AutoControlOvUvBySubstation(TRUE);
        return;
    }
    if (currentSubBus != NULL)
    {
        AutoControlOvUvBySubBus(TRUE);
        return;
    }
    return;
}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::AutoEnableOvUvByArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.                              

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = TRUE;

        string text1 = string("Auto Enable OvUv By Area Control Point");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), 0, 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));

    }
    else if (currentSpArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = FALSE;

        string text1 = string("Auto Enable OvUv By Special Area Control Point");
        string additional1 = string("Special Area: ");
        additional1 += currentSpArea->getPAOName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0,0, 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));

        
    }
    else
        return;

    if (isValidIdFlag)
    {

        CtiCCSubstationPtr currentStation = NULL;
        CtiCCSubstationBusPtr currentSubstationBus = NULL;
        CtiCCFeederPtr currentFeeder = NULL;

        std::list<long>::iterator subIter;
        std::list<long>* stationList = NULL;

        if(isAreaFlag)
        {
            currentArea->setOvUvDisabledFlag(FALSE);

            subIter = currentArea->getSubStationList()->begin();
            stationList = currentArea->getSubStationList();
        }
        else
        {
            currentSpArea->setOvUvDisabledFlag(FALSE);

            subIter = currentSpArea->getSubstationIds()->begin();
            stationList = currentSpArea->getSubstationIds();
        }

        if (stationList == NULL)
        {
            return;
        }

        while (subIter != stationList->end() )
        {
            currentStation = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (currentStation != NULL)
            {
                currentStation->setOvUvDisabledFlag(FALSE);

                CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                for(LONG i=0;i<ccSubstationBuses.size();i++)
                {
                     currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    if (currentSubstationBus->getParentId() == currentStation->getPAOId())
                    {
                        string text1 = string("SubBus: ");
                        text1 += currentSubstationBus->getPAOName();
                        text1 += string("Auto Enable OvUv");

                        currentSubstationBus->setOvUvDisabledFlag(FALSE);
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        if (isAreaFlag)
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(),  currentStation->getPAOId(),  currentSubstationBus->getPAOId(), 0, capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text1, _command->getUser()));
                        else
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0,  currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text1, _command->getUser()));

                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                            currentFeeder->setOvUvDisabledFlag(FALSE);
                            
                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                                if ( currentCapBank->getReEnableOvUvFlag() ) 
                                {
                                    currentCapBank->setReEnableOvUvFlag(FALSE);
                                    CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::ENABLE_OVUV, currentCapBank->getControlDeviceId());
                                    actionMsg->setUser(_command->getUser());
                                    actionMulti->insert(actionMsg);
                                }
                            }
                            
                        }
                        modifiedSubsList.push_back(currentSubstationBus);
                        
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
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = NULL;
        if (isAreaFlag)
        {
            executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
            executor->Execute();
            delete executor;
        }
        else
        {
            executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
            executor->Execute();
            delete executor;
        }
        executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
        executor->Execute();
        delete executor;

        executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
        executor->Execute();
        delete executor;
    }

}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::AutoDisableOvUvByArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.                              

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = TRUE;

        string text1 = string("Auto Disable OvUv By Area Control Point");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), 0, 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));

    }
    else if (currentSpArea != NULL)
    {
            isValidIdFlag = TRUE;
            isAreaFlag = FALSE;

            string text1 = string("Auto Disable OvUv By Special Area Control Point");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPAOName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, 0, 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));

    }
    else
        return;

    if (isValidIdFlag)
    {
        CtiCCSubstationPtr currentStation = NULL;
        CtiCCSubstationBusPtr currentSubstationBus = NULL;
        CtiCCFeederPtr currentFeeder = NULL;


        std::list <long>::iterator subIter;
        std::list<long>* stationList = NULL;

        if(isAreaFlag)
        {     
            currentArea->setOvUvDisabledFlag(TRUE);

            subIter = currentArea->getSubStationList()->begin();
            stationList = currentArea->getSubStationList();
        }
        else
        {
            currentSpArea->setOvUvDisabledFlag(TRUE);

            subIter = currentSpArea->getSubstationIds()->begin();
            stationList = currentSpArea->getSubstationIds();
        }

        if (stationList == NULL)
        {
            return;
        }

        while (subIter != stationList->end() )
        {
            currentStation = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (currentStation != NULL)
            {
                currentStation->setOvUvDisabledFlag(TRUE);

                CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                for(LONG i=0;i<ccSubstationBuses.size();i++)
                {
                    currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    if (currentSubstationBus->getParentId() == currentStation->getPAOId())
                    {
                        string text1 = string("SubBus: ");
                        text1 += currentSubstationBus->getPAOName();
                        text1 += string("Auto Disable OvUv");

                        currentSubstationBus->setOvUvDisabledFlag(TRUE);
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        if (isAreaFlag)
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPAOId(), currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                        else
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, currentStation->getPAOId(), currentSubstationBus->getPAOId(), 0, capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));


                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                            currentFeeder->setOvUvDisabledFlag(TRUE);
                            
                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                                if ( !currentCapBank->getOvUvDisabledFlag() ) 
                                {
                                    currentCapBank->setReEnableOvUvFlag(TRUE);
                                    CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::DISABLE_OVUV, currentCapBank->getControlDeviceId());
                                    actionMsg->setUser(_command->getUser());
                                    actionMulti->insert(actionMsg);
                                }
                            }
                            
                        }
                        modifiedSubsList.push_back(currentSubstationBus);
                        
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
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = NULL;
        if (isAreaFlag)
        {
            executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
            executor->Execute();
            delete executor;
        }
        else
        {
            executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
            executor->Execute();
            delete executor;
        }
        executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
        executor->Execute();
        delete executor;

        executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
        executor->Execute();
        delete executor;
    }

}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::AutoControlOvUvBySubstation(BOOL disableFlag)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
    CtiCCSubstationBusPtr currentSubBus = NULL;

    if (currentStation != NULL)
    {

        string text1 = string("Auto ");
        if (disableFlag)
            text1 += "Disable OvUv By Substation Control Point";
        else
            text1 += "Enable OvUv By Substation Control Point";

        string additional1 = string("Substation: ");
        additional1 += currentStation->getPAOName();

        if (disableFlag)
        {
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(), 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));
        }
        else
        {
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(), 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));
        }


        std::list <long>::iterator busIter;
        if (disableFlag)
            currentStation->setOvUvDisabledFlag(TRUE);
        else
            currentStation->setOvUvDisabledFlag(FALSE);

        busIter = currentStation->getCCSubIds()->begin();
    
        while (busIter != currentStation->getCCSubIds()->end() )
        {
            currentSubBus = store->findSubBusByPAObjectID(*busIter);
            busIter++;

            if (currentSubBus != NULL)
            {          
                string text1 = string("SubBus: ");
                text1 += currentSubBus->getPAOName();
                if (disableFlag)
                {
                    currentSubBus->setOvUvDisabledFlag(TRUE);
                    text1 += string("Auto Disable OvUv");

                    currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(), currentSubBus->getPAOId(), 0, capControlDisableOvUv, currentSubBus->getEventSequence(), 0, text1, _command->getUser()));
                }
                else
                {
                    currentSubBus->setOvUvDisabledFlag(FALSE);
                    text1 += string("Auto Enable OvUv");

                    currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(),  currentSubBus->getPAOId(), 0, capControlEnableOvUv, currentSubBus->getEventSequence(), 1, text1, _command->getUser()));

                }

                CtiFeeder_vec& ccFeeders = currentSubBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    if (disableFlag)
                        currentFeeder->setOvUvDisabledFlag(TRUE);
                    else
                        currentFeeder->setOvUvDisabledFlag(FALSE);

                    
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                        if (disableFlag)
                        {
                            if ( !currentCapBank->getOvUvDisabledFlag() ) 
                            {
                                currentCapBank->setReEnableOvUvFlag(TRUE);
                                CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::DISABLE_OVUV, currentCapBank->getControlDeviceId());
                                actionMsg->setUser(_command->getUser());
                                actionMulti->insert(actionMsg);
                            }
                            
                        }
                        else
                        {
                            if ( currentCapBank->getReEnableOvUvFlag() )  
                            {
                                currentCapBank->setReEnableOvUvFlag(FALSE);
                                CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::ENABLE_OVUV, currentCapBank->getControlDeviceId());
                                actionMsg->setUser(_command->getUser());
                                actionMulti->insert(actionMsg);
                            }
                      
                        }
                    }
                    
                }
                modifiedSubsList.push_back(currentSubBus);
                        
            }
        }

        if (actionMulti->getCount() > 0)
        {
           CtiCCExecutorFactory f;
           CtiCCExecutor* executor = f.createExecutor(actionMulti);
           executor->Execute();
           delete executor;
        }
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = NULL;
        
        executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
        executor->Execute();
        delete executor;

        executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
        executor->Execute();
        delete executor;

        executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
        executor->Execute();
        delete executor;
    }

}   

/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::AutoControlOvUvBySubBus(BOOL disableFlag)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCSubstationPtr currentStation = NULL;
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(subId);
    
    if (currentSubBus != NULL)
    {
        currentStation = store->findSubstationByPAObjectID(currentSubBus->getParentId());
        if (currentStation != NULL)
        {   
            string text1 = string("Auto ");
            if (disableFlag)
            {
                text1 += "Disable OvUv By SubBus Control Point";
                currentSubBus->setOvUvDisabledFlag(TRUE);
            
                currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(), currentSubBus->getPAOId(), 0, capControlDisableOvUv, currentSubBus->getEventSequence(), 0, text1, _command->getUser()));
            }
            else
            {
                text1 += "Enable OvUv By SubBus Control Point";
                currentSubBus->setOvUvDisabledFlag(FALSE);
            
                currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPAOId(), currentSubBus->getPAOId(), 0, capControlEnableOvUv, currentSubBus->getEventSequence(), 1, text1, _command->getUser()));
            
            }
            string additional1 = string("SubBus: ");
            additional1 += currentSubBus->getPAOName();
            
            CtiFeeder_vec& ccFeeders = currentSubBus->getCCFeeders();
            
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            
                if (disableFlag)
                    currentFeeder->setOvUvDisabledFlag(TRUE);
                else
                    currentFeeder->setOvUvDisabledFlag(FALSE);
            
                
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
            
                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
            
                    if (disableFlag)
                    {
                        if ( !currentCapBank->getOvUvDisabledFlag() )  
                        {
                            currentCapBank->setReEnableOvUvFlag(TRUE);
                            CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::DISABLE_OVUV, currentCapBank->getControlDeviceId());
                            actionMsg->setUser(_command->getUser());
                            actionMulti->insert(actionMsg);
                        }
                        
                    }
                    else
                    {
                        if ( currentCapBank->getReEnableOvUvFlag() ) 
                        {
                            currentCapBank->setReEnableOvUvFlag(FALSE);
                            CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::ENABLE_OVUV, currentCapBank->getControlDeviceId());
                            actionMsg->setUser(_command->getUser());
                            actionMulti->insert(actionMsg);
                        }
            
                    }
                }
                
            }
            modifiedSubsList.push_back(currentSubBus);
                    
            
            
            
            if (actionMulti->getCount() > 0)
            {
               CtiCCExecutorFactory f;
               CtiCCExecutor* executor = f.createExecutor(actionMulti);
               executor->Execute();
               delete executor;
            }
            else
            {
                delete actionMulti;
            }
            
            if (eventMulti->getCount() > 0)
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            else
                delete eventMulti;
            if (multi->getCount() > 0)
                CtiCapController::getInstance()->sendMessageToDispatch(multi);
            else
                delete multi;
            
            CtiCCExecutorFactory f;
            CtiCCExecutor *executor = NULL;
            
            executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
            executor->Execute();
            delete executor;

            executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
            executor->Execute();
            delete executor;
            
            executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
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
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    { 
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (currentArea->getReEnableAreaFlag())
            {                                      
                currentArea->setDisableFlag(FALSE);
                store->UpdateAreaDisableFlagInDB(currentArea);
            }
        }
    }
               
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    else
        delete multi;

    
    CtiCCExecutorFactory f;
    CtiCCExecutor*executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
    executor->Execute();
    delete executor;

    CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::SYSTEM_STATUS, 1);
    actionMsg->setUser(_command->getUser());
    executor = f.createExecutor(actionMsg);
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
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    { 
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (!currentArea->getDisableFlag())
            {
                currentArea->setReEnableAreaFlag(TRUE);
                currentArea->setDisableFlag(TRUE);
                store->UpdateAreaDisableFlagInDB(currentArea);
            }
        }
    }
               
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    else
        delete multi;

    
    CtiCCExecutorFactory f;
    CtiCCExecutor*executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
    executor->Execute();
    delete executor;

    CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::SYSTEM_STATUS, 0);
    actionMsg->setUser(_command->getUser());
    executor = f.createExecutor(actionMsg);
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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());

                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::Open ) 
                            {
                                //currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                                       currentCapBank, TRUE);

                            }
                            else
                            {
                                //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                                       currentCapBank, TRUE);

                            }
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
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
                                DOUBLE kvarBefore = 0;

                                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                }
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);

                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text,
                                                                        _command->getUser(), kvarBefore, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));
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

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            BOOL confirmImmediately = FALSE;

                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::TimeOfDayMethod) )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
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

                    string text = string("Manual Scan 2-Way CBC attached to CapBank: ");
                    text += currentCapBank->getPAOName();
                    string additional = string("CBC: ");
                    CHAR devID[20];
                    additional += string (ltoa(currentCapBank->getControlDeviceId(),devID,10));

                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                    ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getControlPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlManualCommand, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

                }
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
    if( cbcID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(cbcID,"scan integrity");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        
    }
    else
    {
        delete multi;
        delete eventMulti;
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
            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                if (!currentFeeder->getDisableFlag())
                {
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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
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
                                //currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                                       currentCapBank, TRUE);


                            }
                            else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                            {
                                text += "Close Sent, Sub VarLoad = ";
                                //currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                                       currentCapBank, TRUE);
                            }
                            currentCapBank->setControlStatusQuality(CC_Normal);

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
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                            currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            ccEvents.push_back(eventMsg);
                            
                            currentCapBank->setBeforeVarsString(" Confirm ");
                            currentCapBank->setAfterVarsString(" --- ");
                            currentCapBank->setPercentChangeString(" --- ");
                            
                            if( controlID > 0 )
                            {
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending) 
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

                            doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);


                        }

                    }
                }
            }
            currentSubstationBus->verifyControlledStatusFlags();
        }
    }
    
    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG fdrId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(fdrId);
    if (currentFeeder == NULL)
    {
        return;
    }
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

    if (currentSubstationBus == NULL)
    {
        return;
    }
    if ((currentFeeder->getStrategyId() > 0 || currentSubstationBus->getStrategyId() > 0) && !currentFeeder->getDisableFlag())
    {
        string text1 = string("Manual Confirm Feeder");
        string additional1 = string("Feeder: ");
        additional1 += currentFeeder->getPAOName();
        if (_LOG_MAPID_INFO) 
        {
            additional1 += " MapID: ";
            additional1 += currentFeeder->getMapLocationId();
            additional1 += " (";
            additional1 += currentFeeder->getPAODescription();
            additional1 += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
        ccEvents.push_back( new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

        
        
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
                currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
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
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                           currentCapBank, TRUE);


                }
                else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                {
                    text += "Close Sent, Sub VarLoad = ";
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                           currentCapBank, TRUE);
                }
                currentCapBank->setControlStatusQuality(CC_Normal);

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
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);                           
                eventMsg->setActionId(currentCapBank->getActionId());
                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                ccEvents.push_back(eventMsg);

                currentCapBank->setBeforeVarsString(" Confirm ");
                currentCapBank->setAfterVarsString(" --- ");
                currentCapBank->setPercentChangeString(" --- ");
                
                if( controlID > 0 )
                {
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending) 
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

                doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);


            }

        }
        currentSubstationBus->verifyControlledStatusFlags();
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() <<  " - Manual Confirm Feeder: "<<currentFeeder->getPAOName() <<" inhibited due to: ";
        if (currentFeeder->getDisableFlag())
        {
            dout << "DisableFlag."<< endl;
        }
        else
        {
            dout << "No Control Method."<< endl;
        }
    }
    
    
    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    
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
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL,0, currentArea->getPAOId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));


        list <LONG>::const_iterator iter = currentArea->getSubStationList()->begin();
        while (iter != currentArea->getSubStationList()->end())
        {   
            LONG stationId = *iter;
            CtiCCSubstation *station = store->findSubstationByPAObjectID(stationId);
            if (station != NULL)
            {                   
                list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                { 
                    LONG busId = *iterBus;
                    CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL)
                    {
                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {
                            if (!currentSubstationBus->getDisableFlag() && !station->getDisableFlag())
                            {   
                                CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::CONFIRM_SUB, currentSubstationBus->getPAOId());
                                actionMsg->setUser(_command->getUser());
                                confirmMulti->insert(actionMsg);
                            }
                        }
                    }
                    iterBus++;
                }
            }
            iter++;
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
        else
            delete confirmMulti;
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

    }
    else 
    {
        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //Make sure this special area is enabled, or kick back an error to the clients.
            if(currentSpArea->getDisableFlag())
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Special Area is not enabled, commands refused." << endl;
                }
    
                CtiCCExecutorFactory f;
                CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::COMMAND_REFUSED, "Special Area is not enabled.");
                msg->setUser(_command->getUser());
                CtiCCExecutor* executor = f.createExecutor(msg);
                executor->Execute();
                delete executor;
            }
            else 
            {
                
                string text1 = string("Manual Confirm Special Area");
                string additional1 = string("Special Area: ");
                additional1 += currentSpArea->getPAOName();
                
                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPAOId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));
    
                list <LONG>::const_iterator iter = currentSpArea->getSubstationIds()->begin();
                while (iter != currentSpArea->getSubstationIds()->end())
                {   
                    LONG stationId = *iter;
                    CtiCCSubstation *station = store->findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {                   
                        list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
                        while (iterBus  != station->getCCSubIds()->end())
                        { 
                            LONG busId = *iterBus;
                            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                            if (currentSubstationBus != NULL)
                            {
                                if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                                {
                                    if (!currentSubstationBus->getDisableFlag() && !station->getDisableFlag())
                                    {    
                                        CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::CONFIRM_SUB, currentSubstationBus->getPAOId());
                                        actionMsg->setUser(_command->getUser());
                                        confirmMulti->insert(actionMsg);
                                    }
                                }
                            }
                            iterBus++;
                        }
                    }
                    iter++;
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
                else
                    delete eventMulti;
    
                if (multi->getCount() > 0)
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                else
                    delete multi;
            }
        }
        else
        {
            CtiCCSubstation* station = store->findSubstationByPAObjectID(areaId);
            if (station != NULL)
            {
                string text1 = string("Manual Confirm Substation");
                string additional1 = string("Substation: ");
                additional1 += station->getPAOName();
                
                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, station->getParentId(), station->getPAOId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                { 
                    LONG busId = *iterBus;
                    CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL)
                    {
                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {
                            if (!currentSubstationBus->getDisableFlag())
                            {    
                                CtiCCCommand* actionMsg = new CtiCCCommand(CtiCCCommand::CONFIRM_SUB, currentSubstationBus->getPAOId());
                                actionMsg->setUser(_command->getUser());
                                confirmMulti->insert(actionMsg);
                            }
                        }
                    }
                    iterBus++;
                }
                if (confirmMulti->getCount() > 0)
                {
                    CtiCCExecutorFactory f;
                    CtiCCExecutor* executor = f.createExecutor(confirmMulti);
                    executor->Execute();
                    delete executor;
        
                    executor = f.createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds())));
                    executor->Execute();
                    delete executor;
                }
                if (eventMulti->getCount() > 0)
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                else
                    delete eventMulti;

                if (multi->getCount() > 0)
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                else
                    delete multi;
            }
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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending, 
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
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
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                ccEvents.push_back(eventMsg);
                                
                                currentCapBank->setBeforeVarsString(" Confirm ");
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

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
                                
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            BOOL confirmImmediately = FALSE;
                            if( savedControlStatus == CtiCCCapBank::Open )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::TimeOfDayMethod) )
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
        else
            delete eventMulti;
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
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
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
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPAOId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending, 
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
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
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                ccEvents.push_back(eventMsg);

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

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            BOOL confirmImmediately = FALSE;
                            if( savedControlStatus == CtiCCCapBank::Close )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::TimeOfDayMethod) )
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
        else
            delete eventMulti;
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
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    doConfirmImmediately
---------------------------------------------------------------------------*/    

void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG bankId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

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
                currentFeeder->setRetryIndex(0);
                if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Close);
                    currentCapBank->setControlStatusQuality(CC_Normal);
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


                        INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                        string stateInfo = currentCapBank->getControlStatusQualityString();
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", 
                                                                _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");


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
                    currentCapBank->setControlStatusQuality(CC_Normal);
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

                        INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                        string stateInfo = currentCapBank->getControlStatusQualityString();
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                 _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");

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
                            currentCapBank->setControlStatusQuality(CC_Normal);
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

                                INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

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
                            currentCapBank->setControlStatusQuality(CC_Normal);
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

                                INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                        _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

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
    executor = f.createExecutor(new CtiCCSubstationsMsg(*(store->getCCSubstations(CtiTime().seconds())), CtiCCSubstationsMsg::AllSubsSent));
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
                originalFeederId = currentCapBank->getOriginalParent().getOriginalParentId();
                capSwitchingOrder = currentCapBank->getOriginalParent().getOriginalSwitchingOrder();
                closeOrder = currentCapBank->getOriginalParent().getOriginalCloseOrder();
                tripOrder = currentCapBank->getOriginalParent().getOriginalTripOrder();
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
    ReturnFeederToOriginalSubBus
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ReturnFeederToOriginalSubBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG fdrId = _command->getId();
    BOOL found = FALSE;
    LONG tempSubBusId = 0;
    LONG movedFeederId = fdrId;
    LONG originalSubBusId = 0;
    float fdrSwitchingOrder = 0.0;
    
    CtiCCSubstationBus* currentSubstationBus = NULL;
    CtiCCFeeder* currentFeeder = NULL;

    currentFeeder = store->findFeederByPAObjectID(fdrId);
    if (currentFeeder != NULL) 
    { 
        currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus == NULL)
            return;
        if (!currentSubstationBus->getVerificationFlag())
        {
            tempSubBusId = currentFeeder->getParentId();
            movedFeederId = fdrId;
            originalSubBusId = currentFeeder->getOriginalParent().getOriginalParentId();
            fdrSwitchingOrder = currentFeeder->getOriginalParent().getOriginalSwitchingOrder();
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                             <<".  Cannot perform RETURN FEEDER on Fdr: " << currentFeeder->getPAOName() << " PAOID: " << currentFeeder->getPAOId() << "."<<endl;
        }
    }

    if( tempSubBusId > 0 && movedFeederId > 0 && originalSubBusId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Move Feeder to original SubBus PAO Id: " << originalSubBusId << endl;
        }
        moveFeeder(1, tempSubBusId, movedFeederId, originalSubBusId, fdrSwitchingOrder);
    }
    else
    {
        if( tempSubBusId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - SubBus not found PAO Id: " << tempSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( originalSubBusId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - SubBus not found PAO Id: " << originalSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << movedFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    
    CtiCCSubstation* currentStation = store->findSubstationByPAObjectID(paoId);
    if (currentStation != NULL)
    {
        list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
        while (iterBus  != currentStation->getCCSubIds()->end())
        { 
            LONG busId = *iterBus;
            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL)
            {
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
            
                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
             
                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        currentCapBank->setCurrentDailyOperations(0);
                        currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                    }
                    currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                    currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                }
                currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                found = TRUE;
            }
            iterBus++;
        }
        string text1 = string("Reset Daily Operations");
        string additional1 = string("Substation: ");
        additional1 += currentStation->getPAOName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));



    }
    else
    {

        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(paoId);
        if (currentSubstationBus != NULL)
        {
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
            
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
      
                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                }
                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
            }
            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

            INT seqId = CCEventSeqIdGen();
            currentSubstationBus->setEventSequence(seqId);
            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
            else
                ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));


            string text1 = string("Reset Daily Operations");
            string additional1 = string("Substation Bus: ");
            additional1 += currentSubstationBus->getPAOName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            found = TRUE;
      
        }  
        else
        {
            CtiCCFeeder* currentFeeder =  store->findFeederByPAObjectID(paoId);
            
            if( currentFeeder != NULL )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
      
                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                }
      
                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

                INT seqId = CCEventSeqIdGen();
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                if (currentFeeder->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(new CtiCCEventLogMsg(0,currentFeeder->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPAOId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPAOId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                string text1 = string("Reset Daily Operations");
                string additional1 = string("Feeder: ");
                additional1 += currentFeeder->getPAOName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));

                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

                if (currentSubstationBus != NULL)
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                }
                found = TRUE;
      
            }
            else
            {
                CtiCCCapBank* currentCapBank = store->findCapBankByPAObjectID(paoId);
                if( currentCapBank != NULL )
                {   
                    currentFeeder = store->findFeederByPAObjectID(currentCapBank->getParentId());
                    currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

                    if (currentSubstationBus != NULL && currentFeeder != NULL)
                    {
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                       
                            INT seqId = CCEventSeqIdGen();
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }
                        else
                        {
                            currentCapBank->setTotalOperations(0);
                        }
                        currentCapBank->setCurrentDailyOperations(0);
                        currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                       
                        string text1 = string("Reset Daily Operations");
                        string additional1 = string("CapBank: ");
                        additional1 += currentCapBank->getPAOName();
                       
                        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                       
                       
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        found = TRUE;
                    }
                }
            }
      
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
        else
            delete multiDispatchMsg;
        if (ccEvents.size() > 0)                                                  
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
            delete eventMulti;
    }
    else
    {
        delete multiDispatchMsg;
        delete eventMulti;

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << "Cannot find PAO Id: " << paoId << " in ResetDailyOperations() in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ResetAllSystemOpCounts()
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

    string text1 = string("Manual System Reset All Op Counts");
    string additional1 = string("CapControl SYSTEM RESET OP COUNTS ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());
    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    
    for (int i = 0; i <ccAreas.size(); i++ )
    {
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);    
        if ( currentArea != NULL && ! currentArea->getDisableFlag())
        {
            std::list <long>::iterator subIter = currentArea->getSubStationList()->begin();
        
            while (subIter != currentArea->getSubStationList()->end())
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(*subIter);
                subIter++;
                
                if (currentStation != NULL)
                {
                    list <LONG>::const_iterator iterBus = currentStation->getCCSubIds()->begin();
                    while (iterBus  != currentStation->getCCSubIds()->end())
                    { 
                        LONG busId = *iterBus;
                        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                        if (currentSubstationBus != NULL)
                        {
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                
                            for(LONG j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                
                                for(LONG k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                    currentCapBank->setCurrentDailyOperations(0);
                                    currentCapBank->setTotalOperations(0);
                                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                                }
                                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                            }
                            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                                ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                            else
                                ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                
                            currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            found = TRUE;
                        }
                        iterBus++;
                    }
                }
            }
        }
    }

               
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    else
        delete multi;

    
   CtiCCExecutorFactory f;
   CtiCCExecutor *executor = NULL;
   executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
   executor->Execute();
   delete executor;
      
   executor = f.createExecutor(new CtiCCSubstationsMsg(ccStations));
   executor->Execute();
   delete executor;

   executor = f.createExecutor(new CtiCCSubstationBusMsg(ccSubstationBuses,CtiCCSubstationBusMsg::AllSubBusesSent ));
   executor->Execute();
   delete executor;
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
                movedCapBankPtr->getOriginalParent().setOriginalParentId(oldFeederPtr->getPAOId());
                movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder(movedCapBankPtr->getControlOrder());
                movedCapBankPtr->getOriginalParent().setOriginalCloseOrder(movedCapBankPtr->getCloseOrder());
                movedCapBankPtr->getOriginalParent().setOriginalTripOrder(movedCapBankPtr->getTripOrder());
            }
            else
            {
                movedCapBankPtr->getOriginalParent().setOriginalParentId(0);
                movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder(0.0);
                movedCapBankPtr->getOriginalParent().setOriginalCloseOrder(0.0);
                movedCapBankPtr->getOriginalParent().setOriginalTripOrder(0.0);

            }

            movedCapBankPtr->setParentId(newFeederId);
          
        }

        {
            CtiCCCapBank_SVector& newFeederCapBanks = newFeederPtr->getCCCapBanks();

            movedCapBankPtr->setControlOrder(capSwitchingOrder);
            movedCapBankPtr->setCloseOrder(closeOrder);
            movedCapBankPtr->setTripOrder(tripOrder);

            newFeederCapBanks.push_back(movedCapBankPtr);

            if( permanentFlag )
            {
                //reorder new feeder.
                newFeederPtr->checkForAndReorderFeeder();
                //reorder old feeder
                oldFeederPtr->checkForAndReorderFeeder();
            }

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
    CtiCCCapBankMoveExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCFeederMoveExecutor::Execute()
{
    INT permanentFlag = _fdrMoveMsg->getPermanentFlag();
    LONG oldSubBusId = _fdrMoveMsg->getOldParentId();
    LONG movedFeederId = _fdrMoveMsg->getObjectId();
    LONG newSubBusId = _fdrMoveMsg->getNewParentId();
    float fdrSwitchingOrder = _fdrMoveMsg->getSwitchingOrder();

    moveFeeder(permanentFlag, oldSubBusId, movedFeederId, newSubBusId, fdrSwitchingOrder);
}

/*---------------------------------------------------------------------------
    moveCapBank
---------------------------------------------------------------------------*/    
void CtiCCExecutor::moveFeeder(INT permanentFlag, LONG oldSubBusId, LONG movedFeederId, LONG newSubBusId, float fdrSwitchingOrder)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBus* oldSubBusPtr = NULL;
    CtiCCSubstationBus* newSubBusPtr = NULL;
    CtiCCFeeder* movedFeederPtr = NULL;


    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    BOOL found = FALSE;
    BOOL verificationFlag = FALSE;

    movedFeederPtr = store->findFeederByPAObjectID(movedFeederId);
    if (movedFeederPtr == NULL) 
        return;

    oldSubBusPtr = store->findSubBusByPAObjectID(oldSubBusId);
    if (oldSubBusPtr != NULL) 
    {
        if (oldSubBusPtr->getVerificationFlag()) 
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< oldSubBusPtr->getPAOName() <<" PAOID: "<< oldSubBusPtr->getPAOId() 
                             <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPAOName()<<" PAOID: "<<movedFeederId<<"."<<endl;
            }
            verificationFlag = TRUE;
            found = TRUE;
        }
        else
        {
            oldSubBusPtr->setBusUpdatedFlag(TRUE);
        }
    }
    newSubBusPtr = store->findSubBusByPAObjectID(newSubBusId);
    if (newSubBusPtr != NULL) 
    {
        if (newSubBusPtr->getVerificationFlag()) 
         {
             {
                 CtiLockGuard<CtiLogger> logger_guard(dout);
                 dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< newSubBusPtr->getPAOName() <<" PAOID: "<< newSubBusPtr->getPAOId() 
                              <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPAOName()<<" PAOID: "<<movedFeederId<<"."<<endl;
             }
             verificationFlag = TRUE;
             found = TRUE;
         }
         else
         {
             newSubBusPtr->setBusUpdatedFlag(TRUE);
         }
    }
   
    if( oldSubBusPtr!=NULL && newSubBusPtr!=NULL && movedFeederPtr!=NULL && !verificationFlag)
    {
        
        CtiFeeder_vec& oldFeeders = oldSubBusPtr->getCCFeeders();

        CtiFeeder_vec::iterator itr = oldFeeders.begin();
        while( itr != oldFeeders.end() )
        {
            if (*itr == movedFeederPtr) {
                itr = oldFeeders.erase( itr );
            }else
                ++itr;
        }

        store->removeItemsFromMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, movedFeederId);
        for (int i = 0; i < movedFeederPtr->getCCCapBanks().size(); i++)
        {
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap,  movedFeederPtr->getCCCapBanks()[i]->getPAOId());
        }


        
        if( !permanentFlag )
        {
            movedFeederPtr->getOriginalParent().setOriginalParentId(oldSubBusPtr->getPAOId());
            movedFeederPtr->getOriginalParent().setOriginalSwitchingOrder(movedFeederPtr->getDisplayOrder());
        }
        else
        {
            movedFeederPtr->getOriginalParent().setOriginalParentId(0);
            movedFeederPtr->getOriginalParent().setOriginalSwitchingOrder(0.0);

        }

        movedFeederPtr->setParentId(newSubBusId);
        movedFeederPtr->setDisplayOrder(fdrSwitchingOrder);
        
        
       
        CtiFeeder_vec& newFeeders = newSubBusPtr->getCCFeeders();
        int insertPoint = newFeeders.size();
        int j = insertPoint;

        while (j > 0)
        {
            if (fdrSwitchingOrder <= ((CtiCCFeeder*)newFeeders.at(j-1))->getDisplayOrder())
            {
                insertPoint =  j - 1;
            }

            j--;
        }
        CtiFeeder_vec& ccF = newSubBusPtr->getCCFeeders();
        ccF.insert( ccF.begin()+insertPoint, movedFeederPtr );
        store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &movedFeederId, &newSubBusId);
        for (int i = 0; i < movedFeederPtr->getCCCapBanks().size(); i++)
        {
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap,  movedFeederPtr->getCCCapBanks()[i]->getPAOId());
        }
        
        store->UpdateFeederSubAssignmentInDB(oldSubBusPtr);
        store->UpdateFeederSubAssignmentInDB(newSubBusPtr);
        
        {
            string typeString = (permanentFlag?"Permanent":"Temporary");

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual "
                 << typeString
                 << " Feeder with PAO Id: "
                 << movedFeederPtr->getPAOId() << ", name: "
                 << movedFeederPtr->getPAOName()
                 << ", was moved from SubBus PAO Id: "
                 << oldSubBusPtr->getPAOId() << ", name: "
                 << oldSubBusPtr->getPAOName() << ", to SubBus PAO Id: "
                 << newSubBusPtr->getPAOId() << ", name: "
                 << newSubBusPtr->getPAOName() << ", with order: "
                 << movedFeederPtr->getDisplayOrder() << endl;
        }

        CtiMultiMsg_vec modifiedSubsList;
        modifiedSubsList.clear();
        modifiedSubsList.push_back(newSubBusPtr);
        modifiedSubsList.push_back(oldSubBusPtr);
        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
        executor->Execute();
        delete executor;


    }
    else
    {
        if (!verificationFlag)
        {    
            if( oldSubBusPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - SubBus not found PAO Id: " << oldSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( newSubBusPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - SubBus not found PAO Id: " << newSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( movedFeederPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder not found PAO Id: " << movedFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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
                                currentFeeder->removeMaxKvar(currentCapBank->getPAOId());
                                logToCCEvent = TRUE; 
                            } 

                            currentCapBank->setBeforeVarsString(" Manual ");
                            currentCapBank->setAfterVarsString(" Manual ");
                            currentCapBank->setPercentChangeString(" --- ");

                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setControlStatus((LONG)value);
                            currentCapBank->setControlRecentlySentFlag(FALSE);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentCapBank->setTagsControlStatus((LONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentCapBank->setPorterRetFailFlag(FALSE);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));

                            string text = "";
                            if (logToCCEvent)  
                            { 
                                text = string("Var: Cancelled by Pending Override, "); 
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));

                            }
                            else
                            {
                                text = "CapBank: ";
                                text += currentCapBank->getPAOName(); 
                                text += " Manual State Change to ";
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);

                            }
                            text += currentCapBank->getControlStatusText();

                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _pointDataMsg->getUser() );
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());

                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                        }
                        currentCapBank->setIgnoreFlag(FALSE);
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

                            if ( currentCapBank->getMaxDailyOps() > 0)
                            {
                                if (value  < currentCapBank->getMaxDailyOps())
                                {
                                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                                }
                            }
                            else
                                currentCapBank->setMaxDailyOpsHitFlag(FALSE);

                            char tempchar[80] = "";
                            string text = "CapBank: ";
                            text+= currentCapBank->getPAOName();
                            text += " Operation Count Change";
                            string additional = "Value = ";
                            _snprintf(tempchar,80,"%.*f",1,value);
                            additional += tempchar;
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId); 
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), text, _pointDataMsg->getUser() )); 
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),text,additional,CapControlLogType,SignalEvent,_pointDataMsg->getUser()));
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
        case CTICCSERVERRESPONSE_ID:
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
        case CTICCOBJECTMOVEMSG_ID:
            ret_val = new CtiCCFeederMoveExecutor( (CtiCCObjectMoveMsg*)message );
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
