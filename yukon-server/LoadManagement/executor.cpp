/*-----------------------------------------------------------------------------
    Filename:  executor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Load Management executor classes.

    Initial Date:  2/13/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "executor.h"
#include "lmserver.h"
#include "lmcontrolareastore.h"
#include "loadmanager.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramcurtailment.h"
#include "lmprogramenergyexchange.h"
#include "lmenergyexchangeoffer.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmprogramdirect.h"
#include "lmcontrolarea.h"
#include "devicetypes.h"
#include "lmcurtailcustomer.h"

#include <rw/collstr.h>

extern BOOL _LM_DEBUG;

/*===========================================================================
    CtiLMCommandExecutor
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMCommandExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    switch ( _command->getCommand() )
    {
        case CtiLMCommand::CHANGE_THRESHOLD:
            ChangeThreshold(results);
            break;
    
        case CtiLMCommand::CHANGE_RESTORE_OFFSET:
            ChangeRestoreOffset(results);
            break;

        case CtiLMCommand::CHANGE_CURRENT_START_TIME:
            ChangeDailyStartTime(results);
            break;

        case CtiLMCommand::CHANGE_CURRENT_STOP_TIME:
            ChangeDailyStopTime(results);
            break;

        case CtiLMCommand::CHANGE_CURRENT_OPERATIONAL_STATE:
            ChangeCurrentOperationalState(results);
            break;

        case CtiLMCommand::ENABLE_CONTROL_AREA:
            EnableControlArea(results);
            break;

        case CtiLMCommand::DISABLE_CONTROL_AREA:
            DisableControlArea(results);
            break;

        case CtiLMCommand::ENABLE_PROGRAM:
            EnableProgram(results);
            break;

        case CtiLMCommand::DISABLE_PROGRAM:
            DisableProgram(results);
            break;

        case CtiLMCommand::REQUEST_ALL_CONTROL_AREAS:
            SendAllControlAreas(results);
            break;

        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - executor.cpp::Execute - unknown command type" << endl;
            }
    }
}

/*---------------------------------------------------------------------------
    ChangeThreshold
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeThreshold(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    ULONG triggerNumber = _command->getNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            RWOrdered& triggers = currentLMControlArea->getLMControlAreaTriggers();
            for(ULONG j=0;j<triggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)triggers[j];
                if( currentLMControlAreaTrigger->getTriggerNumber() == triggerNumber )
                {
                    currentLMControlAreaTrigger->setThreshold(_command->getValue());
                    CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentLMControlArea, currentLMControlAreaTrigger);
                    currentLMControlArea->setUpdatedFlag(TRUE);
                    {
                        char tempchar[80] = "";
                        RWCString text = RWCString("User Threshold Change");
                        RWCString additional = RWCString("Threshold for Trigger: ");
                        _snprintf(tempchar,80,"%d",triggerNumber);
                        additional += tempchar;
                        additional += " changed in LMControlArea: ";
                        additional += currentLMControlArea->getPAOName();
                        additional += " PAO ID: ";
                        _snprintf(tempchar,80,"%d",currentLMControlArea->getPAOId());
                        additional += tempchar;
                        additional += " to: ";
                        _snprintf(tempchar,80,"%.*f",3,_command->getValue());
                        additional += tempchar;
                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }
                    break;
                }
            }
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    ChangeRestoreOffset
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeRestoreOffset(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    ULONG triggerNumber = _command->getNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            RWOrdered& triggers = currentLMControlArea->getLMControlAreaTriggers();
            for(ULONG j=0;j<triggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)triggers[j];
                if( currentLMControlAreaTrigger->getTriggerNumber() == triggerNumber )
                {
                    currentLMControlAreaTrigger->setMinRestoreOffset(_command->getValue());
                    CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentLMControlArea, currentLMControlAreaTrigger);
                    currentLMControlArea->setUpdatedFlag(TRUE);
                    {
                        char tempchar[80] = "";
                        RWCString text = RWCString("User Restore Offset Change");
                        RWCString additional = RWCString("Restore Offset for Trigger: ");
                        _snprintf(tempchar,80,"%d",triggerNumber);
                        additional += tempchar;
                        additional += " changed in LMControlArea: ";
                        additional += currentLMControlArea->getPAOName();
                        additional += " PAO ID: ";
                        _snprintf(tempchar,80,"%d",currentLMControlArea->getPAOId());
                        additional += tempchar;
                        additional += " to: ";
                        _snprintf(tempchar,80,"%.*f",3,_command->getValue());
                        additional += tempchar;
                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }
                    break;
                }
            }
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    ChangeCurrentOperationalState
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeCurrentOperationalState(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
}

/*---------------------------------------------------------------------------
    EnableControlArea
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::EnableControlArea(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Enabling LMControlArea: " << currentLMControlArea->getPAOName()
                              << " PAO ID: " << currentLMControlArea->getPAOId() << endl;
            }
            currentLMControlArea->setDisableFlag(FALSE);
            CtiLMControlAreaStore::getInstance()->UpdateControlAreaDisableFlagInDB(currentLMControlArea);
            currentLMControlArea->setUpdatedFlag(TRUE);
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    DisableControlArea
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::DisableControlArea(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Disabling LMControlArea: " << currentLMControlArea->getPAOName()
                              << " PAO ID: " << currentLMControlArea->getPAOId() << endl;
            }
            currentLMControlArea->setDisableFlag(TRUE);
            RWOrdered& lmPrograms = currentLMControlArea->getLMPrograms();
            for(ULONG j=0;j<lmPrograms.entries();j++)
            {
                CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];

                if( currentLMProgramBase->getProgramState() != CtiLMProgramBase::InactiveState )
                {
                    currentLMProgramBase->setProgramState(CtiLMProgramBase::StoppingState);
                    currentLMProgramBase->setManualControlReceivedFlag(TRUE);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Stopping control in LMProgram: " << currentLMProgramBase->getPAOName()
                                      << " because of disable control area message." << endl;
                    }
                }
            }
            CtiLMControlAreaStore::getInstance()->UpdateControlAreaDisableFlagInDB(currentLMControlArea);
            currentLMControlArea->setUpdatedFlag(TRUE);
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    EnableProgram
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::EnableProgram(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(ULONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( commandPAOID == currentLMProgramBase->getPAOId() )
            {
                currentLMProgramBase->setDisableFlag(FALSE);
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Enabling LMProgram: " << currentLMProgramBase->getPAOName()
                                  << " PAO ID: " << currentLMProgramBase->getPAOId() << endl;
                }
                CtiLMControlAreaStore::getInstance()->UpdateProgramDisableFlagInDB(currentLMProgramBase);
                ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    DisableProgram
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::DisableProgram(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(ULONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( commandPAOID == currentLMProgramBase->getPAOId() )
            {
                currentLMProgramBase->setDisableFlag(TRUE);
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Disabling LMProgram: " << currentLMProgramBase->getPAOName()
                                  << " PAO ID: " << currentLMProgramBase->getPAOId() << endl;
                }

                if( currentLMProgramBase->getProgramState() != CtiLMProgramBase::InactiveState )
                {
                    currentLMProgramBase->setProgramState(CtiLMProgramBase::StoppingState);
                    currentLMProgramBase->setManualControlReceivedFlag(TRUE);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Stopping control in LMProgram: " << currentLMProgramBase->getPAOName()
                                      << " because of disable program message." << endl;
                    }
                }
                CtiLMControlAreaStore::getInstance()->UpdateProgramDisableFlagInDB(currentLMProgramBase);
                ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    SendAllControlAreas
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::SendAllControlAreas(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMExecutorFactory f;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(*(store->getControlAreas())));
    executor->Execute(queue);
    delete executor;
}

/*---------------------------------------------------------------------------
    ChangeDailyStartTime
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeDailyStartTime(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    ULONG newStartTime = (ULONG)_command->getValue();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            currentLMControlArea->setCurrentDailyStartTime(newStartTime);
            currentLMControlArea->setUpdatedFlag(TRUE);
            {
                char tempchar[80] = "";
                RWCString text = RWCString("User Daily Start Change");
                RWCString additional = RWCString("New Daily Start Time: ");
                ULONG startTimeHours = newStartTime / 3600;
                ULONG startTimeMinutes = (newStartTime - (startTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",startTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",startTimeMinutes);
                additional += tempchar;
                additional += " changed in LMControlArea: ";
                additional += currentLMControlArea->getPAOName();
                additional += " PAO ID: ";
                _snprintf(tempchar,80,"%d",currentLMControlArea->getPAOId());
                additional += tempchar;
                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            }
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    ChangeDailyStopTime
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeDailyStopTime(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG commandPAOID = _command->getPAOId();
    ULONG newStopTime = (ULONG)_command->getValue();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *(store->getControlAreas());

    for(ULONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            currentLMControlArea->setCurrentDailyStopTime(newStopTime);
            currentLMControlArea->setUpdatedFlag(TRUE);
            {
                char tempchar[80] = "";
                RWCString text = RWCString("User Daily Stop Change");
                RWCString additional = RWCString("New Daily Stop Time: ");
                ULONG stopTimeHours = newStopTime / 3600;
                ULONG stopTimeMinutes = (newStopTime - (stopTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",stopTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",stopTimeMinutes);
                additional += tempchar;
                additional += " changed in LMControlArea: ";
                additional += currentLMControlArea->getPAOName();
                additional += " PAO ID: ";
                _snprintf(tempchar,80,"%d",currentLMControlArea->getPAOId());
                additional += tempchar;
                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            }
            break;
        }
    }
}


/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMManualControlMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    switch ( _controlMsg->getCommand() )
    {
    case CtiLMManualControlMsg::SCHEDULED_START:
        ScheduledStart(results);
        break;

    case CtiLMManualControlMsg::SCHEDULED_STOP:
        ScheduledStop(results);
        break;

    case CtiLMManualControlMsg::START_NOW:
        StartNow(results);
        break;

    case CtiLMManualControlMsg::STOP_NOW:
        StopNow(results);
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - executor.cpp::Execute - unknown command type at: " << __LINE__ << endl;
        }

    }
}

/*---------------------------------------------------------------------------
    ScheduledStart
---------------------------------------------------------------------------*/    
void CtiLMManualControlMsgExecutor::ScheduledStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL found = FALSE;
    ULONG directProgramID = _controlMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( directProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            /*if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual direct control scheduled start received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                _ultoa(_controlMsg->getStartGear(),tempchar,10);
                                dout << RWTime() << " - start gear: " << tempchar << endl;
                                _ultoa(_controlMsg->getStartPriority(),tempchar,10);
                                dout << RWTime() << " - start priority: " << tempchar << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }*/
                            CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                            lmProgramDirect->setManualControlReceivedFlag(FALSE);
                            lmProgramDirect->setProgramState(CtiLMProgramBase::ScheduledState);
                            //lmProgramDirect->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramDirect->setActionDateTime(RWDBDateTime());
                            //lmProgramDirect->setNotificationDateTime(_controlMsg->getNotifyTime());
                            lmProgramDirect->setDirectStartTime(_controlMsg->getStartTime());
                            lmProgramDirect->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramDirect->setDirectStopTime(_controlMsg->getStopTime());
                            lmProgramDirect->setCurrentGearNumber(_controlMsg->getStartGear()-1);
                            if( _controlMsg->getStartPriority() > currentControlArea->getCurrentPriority() )
                            {
                                currentControlArea->setCurrentPriority(_controlMsg->getStartPriority());
                            }
                            //lmProgramDirect->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            //lmProgramDirect->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            //lmProgramDirect->addLMCurtailProgramActivityTable();
                            //lmProgramDirect->dumpDynamicData();
                            {
                                RWCString text = RWCString("Scheduled Manual Start, LM Program: ");
                                text += lmProgramDirect->getPAOName();
                                RWCString additional = RWCString("Start: ");
                                additional += lmProgramDirect->getDirectStartTime().asString();
                                additional += ", Stop: ";
                                additional += lmProgramDirect->getDirectStopTime().asString();
                                CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
                            }
                            lmProgramDirect->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual curtail scheduled start received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - action time: " << RWDBDateTime().asString() << endl;
                                dout << RWTime() << " - notify time: " << _controlMsg->getNotifyTime().asString() << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }
                            CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                            lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
                            lmProgramCurtailment->setProgramState(CtiLMProgramBase::ScheduledState);
                            lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            lmProgramCurtailment->setActionDateTime(RWDBDateTime());
                            lmProgramCurtailment->setNotificationDateTime(_controlMsg->getNotifyTime());
                            lmProgramCurtailment->setCurtailmentStartTime(_controlMsg->getStartTime());
                            lmProgramCurtailment->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramCurtailment->setCurtailmentStopTime(_controlMsg->getStopTime());
                            lmProgramCurtailment->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            if( _controlMsg->getAdditionalInfo().length() > 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            }
                            else
                            {
                                lmProgramCurtailment->setAdditionalInfo("none");
                            }
                            lmProgramCurtailment->addLMCurtailProgramActivityTable();
                            lmProgramCurtailment->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMManualControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    ScheduledStop
---------------------------------------------------------------------------*/    
void CtiLMManualControlMsgExecutor::ScheduledStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL found = FALSE;
    ULONG directProgramID = _controlMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( directProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            /*if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual direct control scheduled stop received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                //_ultoa(_controlMsg->getStartGear(),tempchar,10);
                                //dout << RWTime() << " - start gear: " << tempchar << endl;
                                //_ultoa(_controlMsg->getStartPriority(),tempchar,10);
                                //dout << RWTime() << " - start priority: " << tempchar << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }*/
                            CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                            lmProgramDirect->setManualControlReceivedFlag(FALSE);
                            //lmProgramDirect->setProgramState(CtiLMProgramBase::StoppingState);
                            //lmProgramDirect->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramDirect->setActionDateTime(RWDBDateTime());
                            //lmProgramDirect->setNotificationDateTime(_controlMsg->getNotifyTime());
                            //lmProgramDirect->setDirectStartTime(_controlMsg->getStartTime());
                            //lmProgramDirect->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramDirect->setDirectStopTime(_controlMsg->getStopTime());
                            //lmProgramDirect->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            //lmProgramDirect->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            //lmProgramDirect->addLMCurtailProgramActivityTable();
                            //lmProgramDirect->dumpDynamicData();
                            {
                                RWCString text = RWCString("Scheduled Manual Stop, LM Program: ");
                                text += lmProgramDirect->getPAOName();
                                RWCString additional = RWCString("Stop: ");
                                additional += lmProgramDirect->getDirectStopTime().asString();
                                CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
                            }
                            lmProgramDirect->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual curtail scheduled stop received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                //dout << RWTime() << " - action time: " << RWDBDateTime().asString() << endl;
                                //dout << RWTime() << " - notify time: " << _controlMsg->getNotifyTime().asString() << endl;
                                //dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }
                            CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                            lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
                            //lmProgramCurtailment->setProgramState(CtiLMProgramBase::ScheduledState);
                            //lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramCurtailment->setActionDateTime(RWDBDateTime());
                            //lmProgramCurtailment->setNotificationDateTime(_controlMsg->getNotifyTime());
                            //lmProgramCurtailment->setCurtailmentStartTime(_controlMsg->getStartTime());
                            //lmProgramCurtailment->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramCurtailment->setCurtailmentStopTime(_controlMsg->getStopTime());
                            //lmProgramCurtailment->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            if( _controlMsg->getAdditionalInfo().length() > 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            }
                            else if( lmProgramCurtailment->getAdditionalInfo().length() == 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo("none");
                            }
                            lmProgramCurtailment->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMManualControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    StartNow
---------------------------------------------------------------------------*/    
void CtiLMManualControlMsgExecutor::StartNow(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL found = FALSE;
    ULONG directProgramID = _controlMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( directProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            /*if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual direct control start now received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                _ultoa(_controlMsg->getStartGear(),tempchar,10);
                                dout << RWTime() << " - start gear: " << tempchar << endl;
                                _ultoa(_controlMsg->getStartPriority(),tempchar,10);
                                dout << RWTime() << " - start priority: " << tempchar << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }*/
                            CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                            lmProgramDirect->setManualControlReceivedFlag(FALSE);
                            lmProgramDirect->setProgramState(CtiLMProgramBase::ScheduledState);
                            //lmProgramDirect->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramDirect->setActionDateTime(RWDBDateTime());
                            //lmProgramDirect->setNotificationDateTime(_controlMsg->getNotifyTime());
                            lmProgramDirect->setDirectStartTime(RWDBDateTime());
                            lmProgramDirect->setStartedControlling(RWDBDateTime());
                            lmProgramDirect->setDirectStopTime(_controlMsg->getStopTime());
                            lmProgramDirect->setCurrentGearNumber(_controlMsg->getStartGear()-1);
                            if( _controlMsg->getStartPriority() > currentControlArea->getCurrentPriority() )
                            {
                                currentControlArea->setCurrentPriority(_controlMsg->getStartPriority());
                            }
                            //lmProgramDirect->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            //lmProgramDirect->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            //lmProgramDirect->addLMCurtailProgramActivityTable();
                            //lmProgramDirect->dumpDynamicData();
                            {
                                RWCString text = RWCString("Manual Start, LM Program: ");
                                text += lmProgramDirect->getPAOName();
                                RWCString additional = RWCString("Start: Immediately, Stop: ");
                                additional += lmProgramDirect->getDirectStopTime().asString();
                                CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
                            }
                            lmProgramDirect->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual curtail start now received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - action time: " << RWDBDateTime().asString() << endl;
                                dout << RWTime() << " - notify time: " << _controlMsg->getNotifyTime().asString() << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }
                            CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                            lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
                            lmProgramCurtailment->setProgramState(CtiLMProgramBase::ScheduledState);
                            lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            lmProgramCurtailment->setActionDateTime(RWDBDateTime());
                            lmProgramCurtailment->setNotificationDateTime(RWDBDateTime());
                            lmProgramCurtailment->setCurtailmentStartTime(_controlMsg->getStartTime());
                            lmProgramCurtailment->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramCurtailment->setCurtailmentStopTime(_controlMsg->getStopTime());
                            lmProgramCurtailment->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            if( _controlMsg->getAdditionalInfo().length() > 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            }
                            else
                            {
                                lmProgramCurtailment->setAdditionalInfo("none");
                            }
                            lmProgramCurtailment->addLMCurtailProgramActivityTable();
                            lmProgramCurtailment->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMManualControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    StopNow
---------------------------------------------------------------------------*/    
void CtiLMManualControlMsgExecutor::StopNow(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    BOOL found = FALSE;
    ULONG directProgramID = _controlMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( directProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            /*if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual direct control stop now received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                //_ultoa(_controlMsg->getStartGear(),tempchar,10);
                                //dout << RWTime() << " - start gear: " << tempchar << endl;
                                //_ultoa(_controlMsg->getStartPriority(),tempchar,10);
                                //dout << RWTime() << " - start priority: " << tempchar << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }*/
                            CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                            lmProgramDirect->setManualControlReceivedFlag(FALSE);
                            //lmProgramDirect->setProgramState(CtiLMProgramBase::StoppingState);
                            //lmProgramDirect->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramDirect->setActionDateTime(RWDBDateTime());
                            //lmProgramDirect->setNotificationDateTime(_controlMsg->getNotifyTime());
                            //lmProgramDirect->setDirectStartTime(_controlMsg->getStartTime());
                            //lmProgramDirect->setStartedControlling(_controlMsg->getStartTime());
                            lmProgramDirect->setDirectStopTime(RWDBDateTime());
                            //lmProgramDirect->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            //lmProgramDirect->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            //lmProgramDirect->addLMCurtailProgramActivityTable();
                            //lmProgramDirect->dumpDynamicData();
                            {
                                RWCString text = RWCString("Manual Stop, LM program: ");
                                text += lmProgramDirect->getPAOName();
                                RWCString additional = RWCString("Stop: Immediately");
                                CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
                            }
                            lmProgramDirect->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            if( _LM_DEBUG )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                char tempchar[64];
                                dout << RWTime() << " - Manual curtail stop now received." << endl;
                                _ultoa(_controlMsg->getPAOId(),tempchar,10);
                                dout << RWTime() << " - pao id: " << tempchar << endl;
                                //dout << RWTime() << " - action time: " << RWDBDateTime().asString() << endl;
                                //dout << RWTime() << " - notify time: " << _controlMsg->getNotifyTime().asString() << endl;
                                //dout << RWTime() << " - start time: " << _controlMsg->getStartTime().asString() << endl;
                                //dout << RWTime() << " - stop time: " << _controlMsg->getStopTime().asString() << endl;
                                dout << RWTime() << " - additional info: " << _controlMsg->getAdditionalInfo() << endl;
                            }
                            CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                            lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
                            lmProgramCurtailment->setProgramState(CtiLMProgramBase::StoppingState);
                            //lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
                            //lmProgramCurtailment->setActionDateTime(RWDBDateTime());
                            //lmProgramCurtailment->setNotificationDateTime(_controlMsg->getNotifyTime());
                            //lmProgramCurtailment->setCurtailmentStartTime(_controlMsg->getStartTime());
                            //lmProgramCurtailment->setStartedControlling(_controlMsg->getStartTime());
                            //lmProgramCurtailment->setCurtailmentStopTime(RWDBDateTime());
                            //lmProgramCurtailment->setRunStatus(CtiLMProgramCurtailment::ScheduledRunStatus);
                            if( _controlMsg->getAdditionalInfo().length() > 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo(_controlMsg->getAdditionalInfo());
                            }
                            else if( lmProgramCurtailment->getAdditionalInfo().length() == 0 )
                            {
                                lmProgramCurtailment->setAdditionalInfo("none");
                            }
                            lmProgramCurtailment->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMManualControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}


/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeControlMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    switch ( _energyExchangeMsg->getCommand() )
    {
    case CtiLMEnergyExchangeControlMsg::NEW_OFFER:
        NewOffer(results);
        break;

    case CtiLMEnergyExchangeControlMsg::OFFER_UPDATE:
        OfferUpdate(results);
        break;

    case CtiLMEnergyExchangeControlMsg::OFFER_REVISION:
        OfferRevision(results);
        break;

    case CtiLMEnergyExchangeControlMsg::CLOSE_OFFER:
        CloseOffer(results);
        break;

    case CtiLMEnergyExchangeControlMsg::CANCEL_OFFER:
        CancelOffer(results);
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - executor.cpp::Execute - unknown command type at: " << __LINE__ << endl;
        }

    }
}

/*---------------------------------------------------------------------------
    NewOffer
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::NewOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << RWTime() << " - NEW energy exchange offer received." << endl;
        _ultoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << RWTime() << " - pao id: " << tempchar << endl;
        dout << RWTime() << " - offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << RWTime() << " - notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << RWTime() << " - expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << RWTime() << " - additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    ULONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( energyExchangeProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                            lmProgramEnergyExchange->setProgramState(CtiLMProgramBase::ManualActiveState);
                            RWOrdered& energyExchangeOffers = lmProgramEnergyExchange->getLMEnergyExchangeOffers();
                            {
                                CtiLMEnergyExchangeOffer* newOffer = new CtiLMEnergyExchangeOffer();
                                newOffer->setPAOId(energyExchangeProgramID);
                                newOffer->setOfferDate(_energyExchangeMsg->getOfferDate());
                                newOffer->setOfferId(0);// This forces the program to create a new ref id
                                newOffer->setRunStatus(CtiLMEnergyExchangeOffer::ScheduledRunStatus);
                                newOffer->addLMEnergyExchangeProgramOfferTable();
                                energyExchangeOffers.insert(newOffer);

                                RWOrdered& offerRevisions = newOffer->getLMEnergyExchangeOfferRevisions();
                                CtiLMEnergyExchangeOfferRevision* newRevision = new CtiLMEnergyExchangeOfferRevision();

                                newRevision->setOfferId(newOffer->getOfferId());// This forces the program to create a new ref id
                                newRevision->setRevisionNumber(0);
                                newRevision->setActionDateTime(RWDBDateTime());
                                newRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                newRevision->setOfferExpirationDateTime(_energyExchangeMsg->getExpirationDateTime());
                                if( _energyExchangeMsg->getAdditionalInfo().length() > 0 )
                                {
                                    newRevision->setAdditionalInfo(_energyExchangeMsg->getAdditionalInfo());
                                }
                                else
                                {
                                    newRevision->setAdditionalInfo("none");
                                }
                                newRevision->addLMEnergyExchangeOfferRevisionTable();
                                offerRevisions.insert(newRevision);

                                RWOrdered& hourlyOffers = newRevision->getLMEnergyExchangeHourlyOffers();
                                for(ULONG k=0;k<HOURS_IN_DAY;k++)
                                {
                                    CtiLMEnergyExchangeHourlyOffer* newHourlyOffer = new CtiLMEnergyExchangeHourlyOffer();
                                    newHourlyOffer->setOfferId(newRevision->getOfferId());
                                    newHourlyOffer->setRevisionNumber(0);
                                    newHourlyOffer->setHour(k);
                                    newHourlyOffer->setPrice(_energyExchangeMsg->getPriceOffered(k));
                                    newHourlyOffer->setAmountRequested(_energyExchangeMsg->getAmountRequested(k));
                                    newHourlyOffer->addLMEnergyExchangeHourlyOfferTable();
                                    hourlyOffers.insert(newHourlyOffer);
                                }
                                lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                                currentControlArea->setUpdatedFlag(TRUE);
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    OfferUpdate
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << RWTime() << " - Energy exchange offer UPDATE received." << endl;
        _ultoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << RWTime() << " - pao id: " << tempchar << endl;
        _ultoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << RWTime() << " - offer id: " << tempchar << endl;
        dout << RWTime() << " - offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << RWTime() << " - notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << RWTime() << " - expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << RWTime() << " - additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    ULONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( energyExchangeProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                            RWOrdered& energyExchangeOffers = lmProgramEnergyExchange->getLMEnergyExchangeOffers();
                            if( energyExchangeOffers.entries() > 0 )
                            {
                                if( lmProgramEnergyExchange->isOfferWithId(_energyExchangeMsg->getOfferId()) )
                                {
                                    lmProgramEnergyExchange->setManualControlReceivedFlag(FALSE);
                                    CtiLMEnergyExchangeOffer* updateOffer = lmProgramEnergyExchange->getOfferWithId(_energyExchangeMsg->getOfferId());
                                    //updateOffer->setPAOId(energyExchangeProgramID);
                                    //updateOffer->setOfferDate(_energyExchangeMsg->getOfferDate());
                                    //updateOffer->setOfferId(0);// This forces the program to create a new ref id
                                    //updateOffer->setRunStatus(CtiLMEnergyExchangeOffer::ScheduledRunStatus);

                                    RWOrdered& offerRevisions = updateOffer->getLMEnergyExchangeOfferRevisions();
                                    CtiLMEnergyExchangeOfferRevision* updateRevision = (CtiLMEnergyExchangeOfferRevision*)offerRevisions[offerRevisions.entries()-1];

                                    //updateRevision->setOfferId(0);// This forces the program to create a new ref id
                                    //updateRevision->setRevisionNumber(0);
                                    updateRevision->setActionDateTime(RWDBDateTime());
                                    updateRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    updateRevision->setOfferExpirationDateTime(_energyExchangeMsg->getExpirationDateTime());
                                    if( _energyExchangeMsg->getAdditionalInfo().length() > 0 )
                                    {
                                        updateRevision->setAdditionalInfo(_energyExchangeMsg->getAdditionalInfo());
                                    }
                                    else if( updateRevision->getAdditionalInfo().length() == 0 )
                                    {
                                        updateRevision->setAdditionalInfo("none");
                                    }
                                    updateRevision->updateLMEnergyExchangeOfferRevisionTable();

                                    RWOrdered& hourlyOffers = updateRevision->getLMEnergyExchangeHourlyOffers();
                                    for(ULONG k=0;k<HOURS_IN_DAY;k++)
                                    {
                                        CtiLMEnergyExchangeHourlyOffer* updateHourlyOffer = (CtiLMEnergyExchangeHourlyOffer*)hourlyOffers[k];
                                        //updateHourlyOffer->setOfferId(0);
                                        //updateHourlyOffer->setRevisionNumber(0);
                                        //updateHourlyOffer->setHour(k);
                                        updateHourlyOffer->setPrice(_energyExchangeMsg->getPriceOffered(k));
                                        updateHourlyOffer->setAmountRequested(_energyExchangeMsg->getAmountRequested(k));
                                        updateHourlyOffer->updateLMEnergyExchangeHourlyOfferTable();
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate no offer to update for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate no offers to update in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    OfferRevision
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::OfferRevision(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << RWTime() << " - Energy exchange offer REVISION received." << endl;
        _ultoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << RWTime() << " - pao id: " << tempchar << endl;
        _ultoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << RWTime() << " - offer id: " << tempchar << endl;
        dout << RWTime() << " - offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << RWTime() << " - notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << RWTime() << " - expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << RWTime() << " - additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    ULONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( energyExchangeProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                            RWOrdered& energyExchangeOffers = lmProgramEnergyExchange->getLMEnergyExchangeOffers();
                            if( energyExchangeOffers.entries() > 0 )
                            {
                                if( lmProgramEnergyExchange->isOfferWithId(_energyExchangeMsg->getOfferId()) )
                                {
                                    lmProgramEnergyExchange->setManualControlReceivedFlag(FALSE);
                                    CtiLMEnergyExchangeOffer* revisionOffer = lmProgramEnergyExchange->getOfferWithId(_energyExchangeMsg->getOfferId());
                                    //revisionOffer->setPAOId(energyExchangeProgramID);
                                    //revisionOffer->setOfferDate(_energyExchangeMsg->getOfferDate());
                                    //revisionOffer->setOfferId(0);// This forces the program to create a new ref id

                                    CtiLMEnergyExchangeOfferRevision* currentRevision = revisionOffer->getCurrentOfferRevision();
                                    if( currentRevision->getOfferExpirationDateTime() > RWDBDateTime() )
                                    {
                                        currentRevision->setOfferExpirationDateTime(RWDBDateTime());
                                        currentRevision->updateLMEnergyExchangeOfferRevisionTable();
                                    }
                                    RWOrdered& offerRevisions = revisionOffer->getLMEnergyExchangeOfferRevisions();
                                    CtiLMEnergyExchangeOfferRevision* newRevision = new CtiLMEnergyExchangeOfferRevision();

                                    newRevision->setOfferId(revisionOffer->getOfferId());
                                    newRevision->setRevisionNumber( currentRevision->getRevisionNumber() + 1 );
                                    newRevision->setActionDateTime(RWDBDateTime());
                                    newRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    newRevision->setOfferExpirationDateTime(_energyExchangeMsg->getExpirationDateTime());
                                    if( _energyExchangeMsg->getAdditionalInfo().length() > 0 )
                                    {
                                        newRevision->setAdditionalInfo(_energyExchangeMsg->getAdditionalInfo());
                                    }
                                    else if( newRevision->getAdditionalInfo().length() == 0 )
                                    {
                                        newRevision->setAdditionalInfo("none");
                                    }
                                    newRevision->addLMEnergyExchangeOfferRevisionTable();
                                    offerRevisions.insert(newRevision);

                                    RWOrdered& hourlyOffers = newRevision->getLMEnergyExchangeHourlyOffers();
                                    for(ULONG k=0;k<HOURS_IN_DAY;k++)
                                    {
                                        CtiLMEnergyExchangeHourlyOffer* newHourlyOffer = new CtiLMEnergyExchangeHourlyOffer();
                                        newHourlyOffer->setOfferId(newRevision->getOfferId());
                                        newHourlyOffer->setRevisionNumber(newRevision->getRevisionNumber());
                                        newHourlyOffer->setHour(k);
                                        newHourlyOffer->setPrice(_energyExchangeMsg->getPriceOffered(k));
                                        newHourlyOffer->setAmountRequested(_energyExchangeMsg->getAmountRequested(k));
                                        newHourlyOffer->addLMEnergyExchangeHourlyOfferTable();
                                        hourlyOffers.insert(newHourlyOffer);
                                    }

                                    //lmProgramEnergyExchange->addLMCurtailProgramActivityTable();
                                    //lmProgramEnergyExchange->dumpDynamicData();
                                    revisionOffer->setRunStatus(CtiLMEnergyExchangeOffer::ScheduledRunStatus);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferRevision no offer to revise for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferRevison no offers to revise in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    CloseOffer
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::CloseOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << RWTime() << " - Energy exchange offer CLOSE received." << endl;
        _ultoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << RWTime() << " - pao id: " << tempchar << endl;
        _ultoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << RWTime() << " - offer id: " << tempchar << endl;
        dout << RWTime() << " - offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << RWTime() << " - notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << RWTime() << " - expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << RWTime() << " - additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    ULONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( energyExchangeProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                            RWOrdered& energyExchangeOffers = lmProgramEnergyExchange->getLMEnergyExchangeOffers();
                            if( energyExchangeOffers.entries() > 0 )
                            {
                                if( lmProgramEnergyExchange->isOfferWithId(_energyExchangeMsg->getOfferId()) )
                                {
                                    lmProgramEnergyExchange->setManualControlReceivedFlag(FALSE);
                                    CtiLMEnergyExchangeOffer* closeOffer = lmProgramEnergyExchange->getOfferWithId(_energyExchangeMsg->getOfferId());
                                    //closeOffer->setPAOId(energyExchangeProgramID);
                                    //closeOffer->setOfferDate(_energyExchangeMsg->getOfferDate());
                                    //closeOffer->setOfferId(0);// This forces the program to create a new ref id

                                    RWOrdered& offerRevisions = closeOffer->getLMEnergyExchangeOfferRevisions();
                                    CtiLMEnergyExchangeOfferRevision* closeRevision = (CtiLMEnergyExchangeOfferRevision*)offerRevisions[offerRevisions.entries()-1];

                                    //closeRevision->setOfferId(0);// This forces the program to create a new ref id
                                    //closeRevision->setRevisionNumber(0);
                                    //closeRevision->setActionDateTime(RWDBDateTime());
                                    //closeRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    closeRevision->setOfferExpirationDateTime(RWDBDateTime());
                                    if( _energyExchangeMsg->getAdditionalInfo().length() > 0 )
                                    {
                                        closeRevision->setAdditionalInfo(_energyExchangeMsg->getAdditionalInfo());
                                    }
                                    else if( closeRevision->getAdditionalInfo().length() == 0 )
                                    {
                                        closeRevision->setAdditionalInfo("none");
                                    }
                                    closeOffer->setRunStatus(CtiLMEnergyExchangeOffer::ClosingRunStatus);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offer to close for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offers to close in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}

/*---------------------------------------------------------------------------
    CancelOffer
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::CancelOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << RWTime() << " - Energy exchange offer CANCEL received." << endl;
        _ultoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << RWTime() << " - pao id: " << tempchar << endl;
        _ultoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << RWTime() << " - offer id: " << tempchar << endl;
        dout << RWTime() << " - offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << RWTime() << " - notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << RWTime() << " - expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << RWTime() << " - additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    ULONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( energyExchangeProgramID == currentLMProgramBase->getPAOId() )
                    {
                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                            RWOrdered& energyExchangeOffers = lmProgramEnergyExchange->getLMEnergyExchangeOffers();
                            if( energyExchangeOffers.entries() > 0 )
                            {
                                if( lmProgramEnergyExchange->isOfferWithId(_energyExchangeMsg->getOfferId()) )
                                {
                                    lmProgramEnergyExchange->setManualControlReceivedFlag(FALSE);
                                    CtiLMEnergyExchangeOffer* cancelOffer = lmProgramEnergyExchange->getOfferWithId(_energyExchangeMsg->getOfferId());
                                    //cancelOffer->setPAOId(energyExchangeProgramID);
                                    //cancelOffer->setOfferDate(_energyExchangeMsg->getOfferDate());
                                    //cancelOffer->setOfferId(0);// This forces the program to create a new ref id

                                    RWOrdered& offerRevisions = cancelOffer->getLMEnergyExchangeOfferRevisions();
                                    CtiLMEnergyExchangeOfferRevision* cancelRevision = (CtiLMEnergyExchangeOfferRevision*)offerRevisions[offerRevisions.entries()-1];

                                    //cancelRevision->setOfferId(0);// This forces the program to create a new ref id
                                    //cancelRevision->setRevisionNumber(0);
                                    //cancelRevision->setActionDateTime(RWDBDateTime());
                                    //cancelRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    cancelRevision->setOfferExpirationDateTime(RWDBDateTime());
                                    if( _energyExchangeMsg->getAdditionalInfo().length() > 0 )
                                    {
                                        cancelRevision->setAdditionalInfo(_energyExchangeMsg->getAdditionalInfo());
                                    }
                                    else if( cancelRevision->getAdditionalInfo().length() == 0 )
                                    {
                                        cancelRevision->setAdditionalInfo("none");
                                    }

                                    //lmProgramEnergyExchange->addLMCurtailProgramActivityTable();
                                    //lmProgramEnergyExchange->dumpDynamicData();
                                    cancelOffer->setRunStatus(CtiLMEnergyExchangeOffer::CanceledRunStatus);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offer to close for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offers to close in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
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
}


/*===========================================================================
    CtiLMControlAreaMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMControlAreaMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMServer::getInstance()->Broadcast(((CtiMessage*)_controlAreaMsg)->replicateMessage());
}


/*===========================================================================
    CtiLMCurtailmentAcknowledgeMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMCurtailmentAcknowledgeMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Curtail acknowledge received." << endl;

        char tempchar[64];
        _ultoa(_curtailAckMsg->getPAOId(),tempchar,10);
        RWCString outString = tempchar;
        dout << RWTime() << " - pao id: " << outString.data() << endl;
        _ultoa(_curtailAckMsg->getCurtailReferenceId(),tempchar,10);
        outString = tempchar;
        dout << RWTime() << " - curtail reference id: " << outString.data() << endl;
        dout << RWTime() << " - acknowledge status: " << _curtailAckMsg->getAcknowledgeStatus() << endl;
        dout << RWTime() << " - ip address of ack user: " << _curtailAckMsg->getIPAddressOfAckUser() << endl;
        dout << RWTime() << " - user id name: " << _curtailAckMsg->getUserIdName() << endl;
        dout << RWTime() << " - name of ack person: " << _curtailAckMsg->getNameOfAckPerson() << endl;
        dout << RWTime() << " - curtailmentnotes: " << _curtailAckMsg->getCurtailmentNotes() << endl;
    }

    RWDBDateTime currentDateTime;
    ULONG curtailmentCustomerID = _curtailAckMsg->getPAOId();
    ULONG curtailReferenceID = _curtailAckMsg->getCurtailReferenceId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        BOOL found = FALSE;
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                        RWOrdered& lmCurtailmentCustomers = lmProgramCurtailment->getLMProgramCurtailmentCustomers();
                        if( lmCurtailmentCustomers.entries() > 0 )
                        {
                            for(ULONG k=0;k<lmCurtailmentCustomers.entries();k++)
                            {
                                CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailmentCustomers[k];

                                if( (curtailmentCustomerID == currentLMCurtailCustomer->getPAOId()) &&
                                    (curtailReferenceID == currentLMCurtailCustomer->getCurtailReferenceId()) )
                                {
                                    currentLMCurtailCustomer->setAcknowledgeStatus(_curtailAckMsg->getAcknowledgeStatus());
                                    currentLMCurtailCustomer->setIPAddressOfAckUser(_curtailAckMsg->getIPAddressOfAckUser());
                                    currentLMCurtailCustomer->setUserIdName(_curtailAckMsg->getUserIdName());
                                    currentLMCurtailCustomer->setNameOfAckPerson(_curtailAckMsg->getNameOfAckPerson());
                                    currentLMCurtailCustomer->setCurtailmentNotes(_curtailAckMsg->getCurtailmentNotes());
                                    currentLMCurtailCustomer->setAckDateTime(currentDateTime);

                                    if( currentDateTime.seconds() > (lmProgramCurtailment->getCurtailmentStartTime().seconds() + lmProgramCurtailment->getAckTimeLimit()) )
                                    {
                                        currentLMCurtailCustomer->setAckLateFlag(TRUE);
                                    }
                                    else
                                    {
                                        currentLMCurtailCustomer->setAckLateFlag(FALSE);
                                    }
                                    currentLMCurtailCustomer->updateLMCurtailCustomerActivityTable();
                                    currentControlArea->setUpdatedFlag(TRUE);
                                    //currentLMCurtailCustomer->dumpDynamicData();
                                    found = TRUE;
                                    break;
                                }
                            }
                        }
                        if(found)
                        {
                            break;
                        }
                    }
                }
            }
            if(found)
            {
                break;
            }
        }
        if(found)
        {
            //store->dumpAllDynamicData();
        }
    }
}


/*===========================================================================
    CtiLMEnergyExchangeAcceptMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeAcceptMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Energy exchange accept received." << endl;

        char tempchar[64];
        _ultoa(_energyExchangeAcceptMsg->getPAOId(),tempchar,10);
        RWCString outString = tempchar;
        dout << RWTime() << " - pao id: " << outString.data() << endl;
        _ultoa(_energyExchangeAcceptMsg->getOfferId(),tempchar,10);
        outString = tempchar;
        dout << RWTime() << " - offer id: " << outString.data() << endl;
        _ultoa(_energyExchangeAcceptMsg->getRevisionNumber(),tempchar,10);
        outString = tempchar;
        dout << RWTime() << " - revision number: " << outString.data() << endl;
        dout << RWTime() << " - accept status: " << _energyExchangeAcceptMsg->getAcceptStatus() << endl;
        dout << RWTime() << " - ip address of accept user: " << _energyExchangeAcceptMsg->getIPAddressOfAcceptUser() << endl;
        dout << RWTime() << " - user id name: " << _energyExchangeAcceptMsg->getUserIdName() << endl;
        dout << RWTime() << " - name of accept person: " << _energyExchangeAcceptMsg->getNameOfAcceptPerson() << endl;
        dout << RWTime() << " - energy exchange notes: " << _energyExchangeAcceptMsg->getEnergyExchangeNotes() << endl;
    }

    RWDBDateTime currentDateTime;
    ULONG customerID = _energyExchangeAcceptMsg->getPAOId();
    ULONG offerID = _energyExchangeAcceptMsg->getOfferId();
    ULONG revisionNumber = _energyExchangeAcceptMsg->getRevisionNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWOrdered& controlAreas = *store->getControlAreas();

    if( controlAreas.entries() > 0 )
    {
        BOOL found = FALSE;
        for(ULONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                    {
                        CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                        RWOrdered& lmEnergyExchangeCustomers = lmProgramEnergyExchange->getLMEnergyExchangeCustomers();
                        if( lmEnergyExchangeCustomers.entries() > 0 )
                        {
                            for(ULONG k=0;k<lmEnergyExchangeCustomers.entries();k++)
                            {
                                CtiLMEnergyExchangeCustomer* currentLMEnergyExchangeCustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[k];

                                if( customerID == currentLMEnergyExchangeCustomer->getPAOId() )
                                {
                                    RWOrdered& lmEnergyExchangeCustomerReplies = currentLMEnergyExchangeCustomer->getLMEnergyExchangeCustomerReplies();
                                    for(long l=lmEnergyExchangeCustomerReplies.entries()-1;l>=0;l--)
                                    {
                                        CtiLMEnergyExchangeCustomerReply* currentLMEnergyExchangeCustomerReply = (CtiLMEnergyExchangeCustomerReply*)lmEnergyExchangeCustomerReplies[l];

                                        if( offerID == currentLMEnergyExchangeCustomerReply->getOfferId() &&
                                            revisionNumber == currentLMEnergyExchangeCustomerReply->getRevisionNumber() )
                                        {
                                            if( lmProgramEnergyExchange->isOfferRevisionOpen(offerID, revisionNumber) )
                                            {
                                                currentLMEnergyExchangeCustomerReply->setAcceptStatus(_energyExchangeAcceptMsg->getAcceptStatus());
                                                currentLMEnergyExchangeCustomerReply->setAcceptDateTime(currentDateTime);
                                                currentLMEnergyExchangeCustomerReply->setIPAddressOfAcceptUser(_energyExchangeAcceptMsg->getIPAddressOfAcceptUser());
                                                currentLMEnergyExchangeCustomerReply->setUserIdName(_energyExchangeAcceptMsg->getUserIdName());
                                                currentLMEnergyExchangeCustomerReply->setNameOfAcceptPerson(_energyExchangeAcceptMsg->getNameOfAcceptPerson());
                                                if( _energyExchangeAcceptMsg->getEnergyExchangeNotes().length() > 0 )
                                                {
                                                    currentLMEnergyExchangeCustomerReply->setEnergyExchangeNotes(_energyExchangeAcceptMsg->getEnergyExchangeNotes());
                                                }
                                                else
                                                {
                                                    currentLMEnergyExchangeCustomerReply->setEnergyExchangeNotes("(none)");
                                                }
                                                currentLMEnergyExchangeCustomerReply->updateLMEnergyExchangeCustomerReplyTable();

                                                RWOrdered& lmHourlyCustomers = currentLMEnergyExchangeCustomerReply->getLMEnergyExchangeHourlyCustomers();
                                                if( currentLMEnergyExchangeCustomerReply->getAcceptStatus() == CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus )
                                                {
                                                    if( lmHourlyCustomers.entries() == 0 )
                                                    {
                                                        currentLMEnergyExchangeCustomerReply->updateLMEnergyExchangeCustomerReplyTable();
                                                        for(ULONG m=0;m<HOURS_IN_DAY;m++)
                                                        {
                                                            CtiLMEnergyExchangeHourlyCustomer* newHourlyCustomer = new CtiLMEnergyExchangeHourlyCustomer();
                                                            newHourlyCustomer->setCustomerId(customerID);
                                                            newHourlyCustomer->setOfferId(offerID);
                                                            newHourlyCustomer->setRevisionNumber(revisionNumber);
                                                            newHourlyCustomer->setHour(m);
                                                            newHourlyCustomer->setAmountCommitted(_energyExchangeAcceptMsg->getAmountCommitted(m));
                                                            newHourlyCustomer->addLMEnergyExchangeHourlyCustomerTable();
                                                            lmHourlyCustomers.insert(newHourlyCustomer);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << RWTime() << " - Accept for offer revision that already contains hourly commitments, in: " << __FILE__ << " at: " << __LINE__ << endl;
                                                    }
                                                }
                                                //currentLMCurtailCustomer->dumpDynamicData();
                                                currentControlArea->setUpdatedFlag(TRUE);
                                            }
                                            else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - Accept for offer revision that has expired, in: " << __FILE__ << " at: " << __LINE__ << endl;
                                            }
                                            found = TRUE;
                                            break;
                                        }
                                    }
                                    if(found)
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                        if(found)
                        {
                            break;
                        }
                    }
                }
            }
            if(found)
            {
                break;
            }
        }
        if(found)
        {
            //store->dumpAllDynamicData();
        }
    }
}


/*===========================================================================
    CtiLMMultiMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMMultiMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiLMExecutorFactory f;
    RWOrdered& messages = _multiMsg->getData();
    while(messages.entries( )>0)
    {
        RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
        CtiMessage* message = (CtiMessage*)(messages.pop());
        if( message != NULL )
        {
            CtiLMExecutor* executor = f.createExecutor(message);
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
    CtiLMForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMForwardMsgToDispatchExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiLoadManager::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
}


/*===========================================================================
    CtiLMShutdown
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes a shutdown on the server
---------------------------------------------------------------------------*/
void CtiLMShutdownExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results )
{
    try
    {
        /*if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down client connection thread..." << endl;
        }*/
    
        CtiLMServer::getInstance()->stop();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        /*if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down the controller thread..." << endl;
        }*/
    
        CtiLoadManager::getInstance()->stop();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        /*if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down the strategystore..." << endl;
        }*/
    
        CtiLMControlAreaStore::deleteInstance();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    
    /*if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Done shutting down" << endl;
        dout << RWTime() << " - done with shutdown executor" << endl;
    }*/
}

/*===========================================================================
    CtiLMExecutorFactory
===========================================================================*/
CtiLMExecutor* CtiLMExecutorFactory::createExecutor(const CtiMessage* message)
{
    CtiLMExecutor* ret_val = 0;
    UINT classId = message->isA();

    switch ( classId )
    {
        case CTILMCOMMAND_ID:
            ret_val = new CtiLMCommandExecutor( (CtiLMCommand*)message );
            break;
    
        case CTILMCONTROLAREA_MSG_ID:
            ret_val = new CtiLMControlAreaMsgExecutor( (CtiLMControlAreaMsg*)message );
            break;
    
        case CTILMMANUALCONTROLMSG_ID:
            ret_val = new CtiLMManualControlMsgExecutor( (CtiLMManualControlMsg*)message );
            break;
    
        case CTILMENERGYEXCHANGECONTROLMSG_ID:
            ret_val = new CtiLMEnergyExchangeControlMsgExecutor( (CtiLMEnergyExchangeControlMsg*)message );
            break;
    
        case CTILMCURTAILMENTACK_MSG_ID:
            ret_val = new CtiLMCurtailmentAcknowledgeMsgExecutor( (CtiLMCurtailmentAcknowledgeMsg*)message );
            break;
    
        case CTILMENERGYEXCHANGEACCEPTMSG_ID:
            ret_val = new CtiLMEnergyExchangeAcceptMsgExecutor( (CtiLMEnergyExchangeAcceptMsg*)message );
            break;
    
        case MSG_POINTDATA:
        case MSG_COMMAND:
            ret_val = new CtiLMForwardMsgToDispatchExecutor( (CtiMessage*)message );
            break;
    
        case MSG_MULTI:
            ret_val = new CtiLMMultiMsgExecutor( (CtiMultiMsg*)message );
            break;
        
        case CTILMSHUTDOWN_ID:
            ret_val = new CtiLMShutdownExecutor();
            break;
    
        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - CtiLMExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            }
    }

    return ret_val;
}
