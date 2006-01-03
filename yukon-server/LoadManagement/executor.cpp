/*-----------------------------------------------------------------------------
    Filename:  executor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Load Management executor classes.

    Initial Date:  2/13/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"

#include "executor.h"
#include "clistener.h"
#include "lmcontrolareastore.h"
#include "loadmanager.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramcurtailment.h"
#include "lmprogramenergyexchange.h"
#include "lmprogramcontrolwindow.h"
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
#include "lmconstraint.h"
#include "ctidate.h"
#include "utility.h"

#include <rw/collstr.h>

extern ULONG _LM_DEBUG;

/*===========================================================================
    CtiLMCommandExecutor
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMCommandExecutor::Execute()
{
    switch ( _command->getCommand() )
    {
        case CtiLMCommand::CHANGE_THRESHOLD:
            ChangeThreshold();
            break;
    
        case CtiLMCommand::CHANGE_RESTORE_OFFSET:
            ChangeRestoreOffset();
            break;

        case CtiLMCommand::CHANGE_CURRENT_START_TIME:
            ChangeDailyStartTime();
            break;

        case CtiLMCommand::CHANGE_CURRENT_STOP_TIME:
            ChangeDailyStopTime();
            break;

        case CtiLMCommand::CHANGE_CURRENT_OPERATIONAL_STATE:
            ChangeCurrentOperationalState();
            break;

        case CtiLMCommand::ENABLE_CONTROL_AREA:
            EnableControlArea();
            break;

        case CtiLMCommand::DISABLE_CONTROL_AREA:
            DisableControlArea();
            break;

        case CtiLMCommand::ENABLE_PROGRAM:
            EnableProgram();
            break;

        case CtiLMCommand::DISABLE_PROGRAM:
            DisableProgram(false);
            break;

        case CtiLMCommand::EMERGENCY_DISABLE_PROGRAM:
            DisableProgram(true);
            break;
        
        case CtiLMCommand::REQUEST_ALL_CONTROL_AREAS:
            SendAllControlAreas();
            break;

        case CtiLMCommand::SHED_GROUP:
            ShedGroup();
            break;

        case CtiLMCommand::SMART_CYCLE_GROUP:
        case CtiLMCommand::TRUE_CYCLE_GROUP:
            CycleGroup();
            break;

        case CtiLMCommand::RESTORE_GROUP:
            RestoreGroup();
            break;

        case CtiLMCommand::ENABLE_GROUP:
            EnableGroup();
            break;

        case CtiLMCommand::DISABLE_GROUP:
            DisableGroup();
            break;

        case CtiLMCommand::CONFIRM_GROUP:
            ConfirmGroup();
            break;
         case CtiLMCommand::RESET_PEAK_POINT_VALUE:
            ResetPeakPointValue();
            break;
        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - executor.cpp::Execute - unknown command type" << endl;
            }
    }
}

/*---------------------------------------------------------------------------
    ChangeThreshold
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeThreshold()
{
    LONG commandPAOID = _command->getPAOId();
    LONG triggerNumber = _command->getNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            RWOrdered& triggers = currentLMControlArea->getLMControlAreaTriggers();
            for(LONG j=0;j<triggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)triggers[j];
                if( currentLMControlAreaTrigger->getTriggerNumber() == triggerNumber )
                {
                    currentLMControlAreaTrigger->setThreshold(_command->getValue());
                    CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentLMControlArea, currentLMControlAreaTrigger);
                    currentLMControlArea->setUpdatedFlag(TRUE);
                    {
                        char tempchar[80] = "";
                        string text = ("User Threshold Change");
                        string additional = ("Threshold for Trigger: ");
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
                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser(), NULL));
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << text << ", " << additional << endl;
                        }
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
void CtiLMCommandExecutor::ChangeRestoreOffset()
{
    LONG commandPAOID = _command->getPAOId();
    LONG triggerNumber = _command->getNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            RWOrdered& triggers = currentLMControlArea->getLMControlAreaTriggers();
            for(LONG j=0;j<triggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)triggers[j];
                if( currentLMControlAreaTrigger->getTriggerNumber() == triggerNumber )
                {
                    currentLMControlAreaTrigger->setMinRestoreOffset(_command->getValue());
                    CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentLMControlArea, currentLMControlAreaTrigger);
                    currentLMControlArea->setUpdatedFlag(TRUE);
                    {
                        char tempchar[80] = "";
                        string text = ("User Restore Offset Change");
                        string additional = ("Restore Offset for Trigger: ");
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
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << text << ", " << additional << endl;
                        }
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
void CtiLMCommandExecutor::ChangeCurrentOperationalState()
{
}

/*---------------------------------------------------------------------------
    EnableControlArea
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::EnableControlArea()
{
    LONG commandPAOID = _command->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            {
                char tempchar[80];
                string text = ("Enabling Control Area: ");
                text += currentLMControlArea->getPAOName();
                string additional = ("PAO Id: ");
                _ltoa(currentLMControlArea->getPAOId(),tempchar,10);
                additional += tempchar;

                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << text << ", " << additional << endl;
                }
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
void CtiLMCommandExecutor::DisableControlArea()
{
    LONG commandPAOID = _command->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            {
                char tempchar[80];
                string text = ("Disabling Control Area: ");
                text += currentLMControlArea->getPAOName();
                string additional = ("PAO Id: ");
                _ltoa(currentLMControlArea->getPAOId(),tempchar,10);
                additional += tempchar;

                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << text << ", " << additional << endl;
                }
            }
            currentLMControlArea->setDisableFlag(TRUE);
            RWOrdered& lmPrograms = currentLMControlArea->getLMPrograms();
            for(LONG j=0;j<lmPrograms.entries();j++)
            {
                CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];

                if( currentLMProgramBase->getProgramState() != CtiLMProgramBase::InactiveState )
                {
                    if( currentLMProgramBase->getPAOType() != TYPE_LMPROGRAM_DIRECT )
                    { // do curtialment types still need this state?
                        currentLMProgramBase->setProgramState(CtiLMProgramBase::StoppingState);
                    }
                    currentLMProgramBase->setManualControlReceivedFlag(TRUE);
                    {
                        char tempchar[80];
                        string text = ("Stopping Control Program: ");
                        text += currentLMProgramBase->getPAOName();
                        string additional = ("Reason: Control Area Disabled");
                        additional += " PAO Id: ";
                        _ltoa(currentLMProgramBase->getPAOId(),tempchar,10);
                        additional += tempchar;

                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << text << ", " << additional << endl;
                        }
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
void CtiLMCommandExecutor::EnableProgram()
{
    LONG commandPAOID = _command->getPAOId();
    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( commandPAOID == currentLMProgramBase->getPAOId() )
            {
                currentLMProgramBase->setDisableFlag(FALSE);
                {
                    char tempchar[80];
                    string text = ("Enabling Program: ");
                    text += currentLMProgramBase->getPAOName();
                    string additional = ("PAO Id: ");
                    _ltoa(currentLMProgramBase->getPAOId(),tempchar,10);
                    additional += tempchar;

                    CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
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
    DisableProgram
    If emergency is true then stop and disable the program without causing
    any commands to be sent.
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::DisableProgram(bool emergency)
{
    LONG commandPAOID = _command->getPAOId();
    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( commandPAOID == currentLMProgramBase->getPAOId() )
            {
                currentLMProgramBase->setDisableFlag(TRUE);
                {
                    char tempchar[80];
                    string text = ("Disabling Program: ");
                    text += currentLMProgramBase->getPAOName();
                    if(emergency)
                       text += " (emergency)";
                    string additional = ("PAO Id: ");
                    _ltoa(currentLMProgramBase->getPAOId(),tempchar,10);
                    additional += tempchar;
                    

                    CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                    }
                }

                if( currentLMProgramBase->getProgramState() != CtiLMProgramBase::InactiveState )
                {
                    if( currentLMProgramBase->getPAOType() != TYPE_LMPROGRAM_DIRECT )
                    { // do curtialment types still need this state?
                        currentLMProgramBase->setProgramState(CtiLMProgramBase::StoppingState);
                    }

                    if(emergency)
                    {
                        //No manual stop please, just set the program inactive immediately
                        currentLMProgramBase->setManualControlReceivedFlag(FALSE);
                        currentLMProgramBase->setProgramState(CtiLMProgramBase::InactiveState);
                    }
                    else
                    {   // Next main loop we want to do a manual stop
                        // I guess the fact that we disabled the program and set the
                        // manual controll received flag means stop the program (!)
                        currentLMProgramBase->setManualControlReceivedFlag(TRUE);
                    }

                    {   // let them know
                        char tempchar[80];
                        string text = ("Stopping Control Program: ");
                        text += currentLMProgramBase->getPAOName();
                        if(emergency)
                            text += " (emergency)";                     
                        string additional = ("Reason: Program Disabled");
                        additional += " PAO Id: ";
                        _ltoa(currentLMProgramBase->getPAOId(),tempchar,10);
                        additional += tempchar;

                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << text << ", " << additional << endl;
                        }
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
void CtiLMCommandExecutor::SendAllControlAreas()
{
    CtiLMExecutorFactory f;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiLMControlAreaMsg* msg = new CtiLMControlAreaMsg(*store->getControlAreas(CtiTime().seconds()),CtiLMControlAreaMsg::AllControlAreasSent);

    CtiLMConnectionPtr connection = _command->getConnection();
    if(connection)
    {
        connection->write(msg);
    }
    else
    {
        CtiLoadManager::getInstance()->sendMessageToClients(msg);
    }
}

/*---------------------------------------------------------------------------
    ChangeDailyStartTime
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeDailyStartTime()
{
    LONG commandPAOID = _command->getPAOId();
    LONG newStartTime = (LONG)_command->getValue();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    if( newStartTime >= 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
            if( currentLMControlArea->getPAOId() == commandPAOID )
            {
                currentLMControlArea->setCurrentDailyStartTime(newStartTime);
                currentLMControlArea->setUpdatedFlag(TRUE);
                {
                    char tempchar[80] = "";
                    string text = ("User Daily Start Change");
                    string additional = ("New Daily Start Time: ");
                    LONG startTimeHours = newStartTime / 3600;
                    LONG startTimeMinutes = (newStartTime - (startTimeHours * 3600)) / 60;
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
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                    }
                }
                break;
            }
        }
    }
}

/*---------------------------------------------------------------------------
    ChangeDailyStopTime
---------------------------------------------------------------------------*/    
void CtiLMCommandExecutor::ChangeDailyStopTime()
{
    LONG commandPAOID = _command->getPAOId();
    LONG newStopTime = (LONG)_command->getValue();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    if( newStopTime >= 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
            if( currentLMControlArea->getPAOId() == commandPAOID )
            {
                currentLMControlArea->setCurrentDailyStopTime(newStopTime);
                currentLMControlArea->setUpdatedFlag(TRUE);
                {
                    char tempchar[80] = "";
                    string text = ("User Daily Stop Change");
                    string additional = ("New Daily Stop Time: ");
                    LONG stopTimeHours = newStopTime / 3600;
                    LONG stopTimeMinutes = (newStopTime - (stopTimeHours * 3600)) / 60;
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
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                    }
                }
                break;
            }
        }
    }
}

void CtiLMCommandExecutor::ShedGroup()
{
    LONG groupID = _command->getPAOId();
    LONG shedTime = _command->getNumber();
    LONG routeId = _command->getAuxId();
    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec program_groups  = ((CtiLMProgramDirect*) currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = program_groups.begin(); k != program_groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;

                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        {
                            char tempchar[80];
                            string text = ("Manual Shed: ");
                            text += currentLMGroup->getPAOName();
                            string additional = ("PAO Id: ");
                            _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                            additional += tempchar;

                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << text << ", " << additional << endl;
                            }
                        }

                        CtiRequestMsg* requestMsg = currentLMGroup->createTimeRefreshRequestMsg(0,shedTime,CtiLMProgramDirect::defaultLMStartPriority);

                        if( routeId > 0 )
                        {
                            requestMsg->setRouteId(routeId);
                        }

                        if( requestMsg != NULL )
                        {
                            CtiTime now;
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            CtiLoadManager::getInstance()->sendMessageToPIL(requestMsg);
                            currentLMGroup->setLastControlSent(now);
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cannot create request in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }

                        found = TRUE;
                        break;
                    }
                }
                if( found )
                    break;
            }
        }
        if( found )
            break;
    }
}

void CtiLMCommandExecutor::CycleGroup()
{
    LONG command = _command->getCommand();
    LONG groupID = _command->getPAOId();
    LONG percent = _command->getNumber();
    LONG period = (LONG)_command->getValue();
    LONG cycleCount = _command->getCount();
    LONG routeId = _command->getAuxId();

    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec program_groups  = ((CtiLMProgramDirect*) currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = program_groups.begin(); k != program_groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;

                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        {
                            char tempchar[80];
                            string text = ("Manual Cycle: ");
                            text += currentLMGroup->getPAOName();
                            string additional = ("PAO Id: ");
                            _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                            additional += tempchar;

                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << text << ", " << additional << endl;
                            }
                        }
                        CtiRequestMsg* requestMsg = NULL;

                        if( command == CtiLMCommand::TRUE_CYCLE_GROUP )
                        {
                            currentLMGroup->createTrueCycleRequestMsg(percent,period,cycleCount,false,CtiLMProgramDirect::defaultLMStartPriority);
                        }
                        else
                        {
                            currentLMGroup->createSmartCycleRequestMsg(percent,period,cycleCount,false,CtiLMProgramDirect::defaultLMStartPriority);
                        }

                        if( routeId > 0 )
                        {
                            requestMsg->setRouteId(routeId);
                        }

                        if( requestMsg != NULL )
                        {
                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            CtiLoadManager::getInstance()->sendMessageToPIL(requestMsg);
                            currentLMGroup->setLastControlSent(CtiTime());
                            currentLMGroup->setGroupControlState(CtiLMGroupBase::ActiveState);
                            ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cannot create request in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }

                        found = TRUE;
                        break;
                    }
                }
                if( found )
                    break;
            }
        }
        if( found )
            break;
    }
}

void CtiLMCommandExecutor::RestoreGroup()
{
    LONG groupID = _command->getPAOId();
    LONG routeId = _command->getAuxId();

    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec program_groups  = ((CtiLMProgramDirect*) currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = program_groups.begin(); k != program_groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;

                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        {
                            char tempchar[80];
                            string text = ("Manual Restore: ");
                            text += currentLMGroup->getPAOName();
                            string additional = ("PAO Id: ");
                            _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                            additional += tempchar;

                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << text << ", " << additional << endl;
                            }
                        }
                        int priority = 11;
                        string controlString = "control restore";
                        CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);

                        if( _LM_DEBUG & LM_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                        }

                        if( routeId > 0 )
                        {
                            requestMsg->setRouteId(routeId);
                        }

                        currentLMGroup->setLastControlString(requestMsg->CommandString());
                        CtiLoadManager::getInstance()->sendMessageToPIL(requestMsg);
                        currentLMGroup->setLastControlSent(CtiTime());
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);

                        found = TRUE;
                        break;
                    }
                }
                if( found )
                    break;
            }
        }
        if( found )
            break;
    }
}

void CtiLMCommandExecutor::EnableGroup()
{
    LONG groupID = _command->getPAOId();

    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec groups  = ((CtiLMProgramDirect*)currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = groups.begin(); k != groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;

                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        if( !found )
                        {
                            char tempchar[80];
                            string text = ("Enabling Group: ");
                            text += currentLMGroup->getPAOName();
                            string additional = ("PAO Id: ");
                            _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                            additional += tempchar;

                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << text << ", " << additional << endl;
                            }
                        }

                        currentLMGroup->setDisableFlag(FALSE);
                        if( !found )
                        {
                            CtiLMControlAreaStore::getInstance()->UpdateGroupDisableFlagInDB(currentLMGroup);
                        }
                        ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);

                        found = TRUE;
                    }
                }
            }
        }
    }
}

void CtiLMCommandExecutor::DisableGroup()
{
    LONG groupID = _command->getPAOId();

    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec groups  = ((CtiLMProgramDirect*)currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = groups.begin(); k != groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;

                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        if( !found )
                        {
                            char tempchar[80];
                            string text = ("Disabling Group: ");
                            text += currentLMGroup->getPAOName();
                            string additional = ("PAO Id: ");
                            _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                            additional += tempchar;

                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << text << ", " << additional << endl;
                            }
                        }

                        if( !found &&
                            currentLMGroup->getGroupControlState() != CtiLMGroupBase::InactiveState )
                        {
                            {
                                char tempchar[80];
                                string text = ("Stopping Control Group: ");
                                text += currentLMProgramBase->getPAOName();
                                string additional = ("Reason: Group Disabled");
                                additional += " PAO Id: ";
                                _ltoa(currentLMProgramBase->getPAOId(),tempchar,10);
                                additional += tempchar;

                                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - " << text << ", " << additional << endl;
                                }
                            }

                            int priority = 11;
                            string controlString = "control restore";
                            CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);

                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Sending restore command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }

                            currentLMGroup->setLastControlString(requestMsg->CommandString());
                            CtiLoadManager::getInstance()->sendMessageToPIL(requestMsg);
                            currentLMGroup->setLastControlSent(CtiTime());
                        }

                        currentLMGroup->resetInternalState();
                        currentLMGroup->setDisableFlag(TRUE);
                        if( !found )
                        {
                            CtiLMControlAreaStore::getInstance()->UpdateGroupDisableFlagInDB(currentLMGroup);
                        }
                        currentLMGroup->setGroupControlState(CtiLMGroupBase::InactiveState);
                        ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);

                        found = TRUE;
                    }
                }
            }
        }
    }
}

void CtiLMCommandExecutor::ConfirmGroup()
{
    LONG groupID = _command->getPAOId();
    LONG routeId = _command->getAuxId();

    bool found = FALSE;
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        RWOrdered& lmPrograms = ((CtiLMControlArea*)controlAreas[i])->getLMPrograms();
        for(LONG j=0;j<lmPrograms.entries();j++)
        {
            CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
            if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec groups  = ((CtiLMProgramDirect*)currentLMProgramBase)->getLMProgramDirectGroups();
                for(CtiLMGroupIter k = groups.begin(); k != groups.end(); k++)
                {
                    CtiLMGroupPtr currentLMGroup  = *k;
                    
                    if( currentLMGroup->getPAOId() == groupID )
                    {
                        string str;
                        char var[128];

                        int confirmExpireInSeconds = 300;
                        strcpy(var, "LOAD_MANAGEMENT_CONFIRM_EXPIRE");
                        if( !(str = gConfigParms.getValueAsString(var)).empty() )
                        {
                            confirmExpireInSeconds = atoi(str.c_str());
                            /*if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << var << ":  " << str << endl;
                            }*/
                        }
                        else
                        {
                            /*CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;*/
                        }

                        if( currentLMGroup->getLastControlSent().seconds() + confirmExpireInSeconds >= CtiTime().seconds() &&
                            currentLMGroup->getLastControlString().length() > 0 )
                        {
                            {
                                char tempchar[80];
                                string text = ("Manual Confirm: ");
                                text += currentLMGroup->getPAOName();
                                string additional = ("PAO Id: ");
                                _ltoa(currentLMGroup->getPAOId(),tempchar,10);
                                additional += tempchar;

                                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - " << text << ", " << additional << endl;
                                }
                            }
                            int priority = 11;
                            string controlString = currentLMGroup->getLastControlString();
                            CtiRequestMsg* requestMsg = new CtiRequestMsg(currentLMGroup->getPAOId(), controlString,0,0,0,0,0,0,priority);

                            if( _LM_DEBUG & LM_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Sending confirm command, LM Group: " << currentLMGroup->getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
                            }

                            if( routeId > 0 )
                            {
                                requestMsg->setRouteId(routeId);
                            }

                            CtiLoadManager::getInstance()->sendMessageToPIL(requestMsg);
                            currentLMGroup->setLastControlSent(CtiTime());
                            ((CtiLMControlArea*)controlAreas[i])->setUpdatedFlag(TRUE);
                        }

                        found = TRUE;
                        break;
                    }
                }
                if( found )
                    break;
            }
        }
        if( found )
            break;
    }
}

/*
 * Resets the peak point value for a control area trigger.
 */
void CtiLMCommandExecutor::ResetPeakPointValue()
{
    LONG commandPAOID = _command->getPAOId();
    LONG triggerNumber = _command->getNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *(store->getControlAreas(CtiTime().seconds()));

    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentLMControlArea->getPAOId() == commandPAOID )
        {
            RWOrdered& triggers = currentLMControlArea->getLMControlAreaTriggers();
            for(LONG j=0;j<triggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)triggers[j];
                if( currentLMControlAreaTrigger->getTriggerNumber() == triggerNumber )
                {
                    currentLMControlAreaTrigger->setPeakPointValue(0.0);
                    CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentLMControlArea, currentLMControlAreaTrigger);
                    currentLMControlArea->setUpdatedFlag(TRUE);
                    {
                        char tempchar[80] = "";
                        string text = ("User Peak Point Value Reset");
                        string additional = ("Peak Point Value Reset for Trigger: ");
                        _snprintf(tempchar,80,"%d",triggerNumber);
                        CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                    break;
                }
            }
            currentLMControlArea->setUpdatedFlag(TRUE);     
            break;
        }
    }
    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " - " << "Received user peak point value reset for control area id: " << commandPAOID << " trigger: " << triggerNumber << endl;
        dout << CtiTime() << " but couldn't locate the correct trigger." << endl;
    }
}

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMManualControlRequestExecutor::Execute()
{
    CtiLMProgramBase* program = NULL;
    CtiLMControlArea* controlArea = NULL;

    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    if(!store->findProgram(_controlMsg->getPAOId(), &program, &controlArea))
    {
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " - Received a manual control message that specified a program that doesn't exist, program id was: " << _controlMsg->getPAOId() << endl;
        dout << CtiTime() << " - Send a response messge here!" << endl;
        return;
    }
    }

    // Set up a response if this was wrapped up in a request message
    // Fill in the response as we figure out what to say to the client
    // and then send it at the end of this function
    CtiServerResponseMsg* response = NULL; 
    if(_request != NULL)
    {
        response = new CtiServerResponseMsg();

    }
    
    CtiTime startTime;
    CtiTime stopTime;

    //prepare to send a response to the clients
    CtiLMProgramConstraintChecker checker((CtiLMProgramDirect&)*program, CtiTime().seconds());
    bool passed_check = false;
    
    switch ( _controlMsg->getCommand() )
    {

    case CtiLMManualControlRequest::SCHEDULED_START:
        if(startTime <= _controlMsg->getStartTime())
        {
            startTime = _controlMsg->getStartTime();
        }
        
    case CtiLMManualControlRequest::START_NOW:
//      startTime = CtiTime();
        stopTime = _controlMsg->getStopTime();


        switch(_controlMsg->getConstraintCmd())
        {
        case CtiLMManualControlRequest::CHECK_CONSTRAINTS:
            passed_check = checker.checkConstraints(     _controlMsg->getStartGear()-1,
                                                         startTime.seconds(),
                                                         stopTime.seconds());

            if(response != NULL)
            {
                if(passed_check)
                {
                    response->setStatus(CtiServerResponseMsg::OK);
                    response->setMessage("Manual Control Request Constraint Check OK");
                }
                else
                {
                    response->setStatus(CtiServerResponseMsg::ERR);
                    response->setMessage("Manual Control Request Constraint Check Violations");             
                }
            }
            //Do not actually start the program
            break;
            
        case CtiLMManualControlRequest::OVERRIDE_CONSTRAINTS:
            ((CtiLMProgramDirect*)program)->setConstraintOverride(true);
            StartProgram(program, controlArea, startTime, stopTime);
            if(response != NULL)
            {
                response->setStatus(CtiServerResponseMsg::OK);
                response->setMessage("Manual Control Request OK, Overriding Constraints");
            }
            break;
            
        case CtiLMManualControlRequest::USE_CONSTRAINTS:
            // Fix up program control window if necessary
            ((CtiLMProgramDirect*)program)->setConstraintOverride(false);
            CoerceStartStopTime(program, startTime, stopTime);
            StartProgram(program, controlArea, startTime, stopTime);
            if(response != NULL)
            {
                response->setStatus(CtiServerResponseMsg::OK);
                response->setMessage("Manual Control Request OK, Using Constraints");
            }       
            break;
        };
        break;
        
    case CtiLMManualControlRequest::SCHEDULED_STOP:
    case CtiLMManualControlRequest::STOP_NOW:
        stopTime = _controlMsg->getStopTime();

        switch(_controlMsg->getConstraintCmd())
        {
        case CtiLMManualControlRequest::CHECK_CONSTRAINTS:
            if(response != NULL)
            {
                response->setStatus(CtiServerResponseMsg::OK);
                response->setMessage("Manual Control Request Constraint Check OK (doesn't do anything on stop)");
            }               
            //Do not actually start the program
            break;
            
        case CtiLMManualControlRequest::OVERRIDE_CONSTRAINTS:
            ((CtiLMProgramDirect*)program)->setConstraintOverride(true);
            StopProgram(program, controlArea, stopTime);
            if(response != NULL)
            {
                response->setStatus(CtiServerResponseMsg::OK);
                response->setMessage("Manual Control Request OK, Overriding Constraints");
            }                       

            break;
            
        case CtiLMManualControlRequest::USE_CONSTRAINTS:
            ((CtiLMProgramDirect*)program)->setConstraintOverride(false);           
            // Fix up program control window if necessary
            startTime = ((const CtiLMProgramDirect*) program)->getDirectStartTime();        
            CoerceStartStopTime(program, startTime, stopTime);
            StopProgram(program, controlArea, stopTime);
            if(response != NULL)
            {
                response->setStatus(CtiServerResponseMsg::OK);
                response->setMessage("Manual Control Request OK, Using Constraints");
            }                               
            break;
        };
        
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - executor.cpp::Execute - unknown command type at: " << __LINE__ << endl;
            dout << CtiTime() << " - Send a response messge here!" << endl;
        }

    }
    //Only send a response if we received a request
    if(response != NULL)
    {
        response->setMessagePriority(1);
        CtiLMManualControlResponse* lmResp = new CtiLMManualControlResponse();
        lmResp->setPAOId(_controlMsg->getPAOId());
        lmResp->setConstraintViolations(checker.getViolations());       
        response->setPayload(lmResp);
        response->setID(_request->getID());     

        //Send the response to all the clients
        CtiLMConnectionPtr connection = _controlMsg->getConnection();
        if(connection)
        {
            connection->write(response);
        }
        else
        {
            CtiLoadManager::getInstance()->sendMessageToClients(response);
        }
    }
}

void CtiLMManualControlRequestExecutor::StartProgram(CtiLMProgramBase* program, CtiLMControlArea* controlArea,
                                                     const CtiTime& start, const CtiTime& stop)
{
    if( program->getPAOType() == TYPE_LMPROGRAM_DIRECT )
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        { //TODO: pull this out into an operator
            CtiLockGuard<CtiLogger> logger_guard(dout);
            char tempchar[64];
            dout << CtiTime() << " - Manual direct control scheduled start received." << endl;
            _ltoa(_controlMsg->getPAOId(),tempchar,10);
            dout << "pao id: " << tempchar << endl;
            dout << "start time: " << start.asString() << endl;
            dout << "stop time: " << stop.asString() << endl;
            _ltoa(_controlMsg->getStartGear(),tempchar,10);
            dout << "start gear: " << tempchar << endl;
            _ltoa(_controlMsg->getStartPriority(),tempchar,10);
            dout << "start priority: " << tempchar << endl;
            dout << "additional info: " << _controlMsg->getAdditionalInfo() << endl;
        }
        CtiLMProgramDirect* directProgram = (CtiLMProgramDirect*) program;
        StartDirectProgram(directProgram, controlArea, start, stop);

        string text = ("Scheduled Manual Start, LM Program: ");
        text += directProgram->getPAOName();
        string additional = ("Start: ");
        additional += directProgram->getDirectStartTime().asString();
        additional += ", Stop: ";
        additional += directProgram->getDirectStopTime().asString();
        CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << text << ", " << additional << endl;
    }
    } // end program->getPAOType() == TYPE_LMPROGRAM_DIRECT 
    else if(program->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT)
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            char tempchar[64];
            dout << CtiTime() << " - Manual curtail scheduled start received." << endl;
            _ltoa(_controlMsg->getPAOId(),tempchar,10);
            dout << "pao id: " << tempchar << endl;
            dout << "notify time: " << _controlMsg->getNotifyTime().asString() << endl;
            dout << "start time: " << start.asString() << endl;
            dout << "stop time: " << stop.asString() << endl;
            dout << "additional info: " << _controlMsg->getAdditionalInfo() << endl;
        }

        CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)program;
        StartCurtailmentProgram(lmProgramCurtailment, controlArea, start, stop);
            
        lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
        lmProgramCurtailment->setProgramState(CtiLMProgramBase::ScheduledState);
        lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
        lmProgramCurtailment->setActionDateTime(CtiTime());
        lmProgramCurtailment->setNotificationDateTime(_controlMsg->getNotifyTime());
        lmProgramCurtailment->setCurtailmentStartTime(start);
        lmProgramCurtailment->setStartedControlling(start);
        lmProgramCurtailment->setCurtailmentStopTime(stop);
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
        controlArea->setUpdatedFlag(TRUE);
    }
    else
    {
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " << __FILE__ << "(" << __LINE__ << ")" << endl;
        dout << CtiTime() << " send a response message here" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    }

}

void CtiLMManualControlRequestExecutor::StopProgram(CtiLMProgramBase* program, CtiLMControlArea* controlArea, const CtiTime& stop)
{
    if(program->getPAOType() == TYPE_LMPROGRAM_DIRECT)
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            char tempchar[64];
            dout << CtiTime() << " - Manual direct control scheduled stop received." << endl;
            _ltoa(_controlMsg->getPAOId(),tempchar,10);
            dout << "pao id: " << tempchar << endl;
            dout << "stop time: " << stop.asString() << endl;
            dout << "additional info: " << _controlMsg->getAdditionalInfo() << endl;
        }

        CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*) program;
        StopDirectProgram(lmProgramDirect, controlArea, stop);

        string text = ("Scheduled Manual Stop, LM Program: ");
        text += lmProgramDirect->getPAOName();
        string additional = ("Stop: ");
        additional += lmProgramDirect->getDirectStopTime().asString();
        CtiLoadManager::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent,_controlMsg->getUser()) );
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << text << ", " << additional << endl;
    }

    }
    else if(program->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT)
    {
        
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            char tempchar[64];
            dout << CtiTime() << " - Manual curtail scheduled stop received." << endl;
            _ltoa(_controlMsg->getPAOId(),tempchar,10);
            dout << "pao id: " << tempchar << endl;
            dout << "notify time: " << _controlMsg->getNotifyTime().asString() << endl;
            dout << "stop time: " << stop.asString() << endl;
            dout << "additional info: " << _controlMsg->getAdditionalInfo() << endl;
        }
        CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)program;
        StopCurtailmentProgram(lmProgramCurtailment, controlArea, stop);
        
    }
    else
    {
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " <<  __FILE__ << "(" << __LINE__ << ")" << endl;
        dout << CtiTime() << " send a response message here" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    }
}

void CtiLMManualControlRequestExecutor::StartDirectProgram(CtiLMProgramDirect* lmProgramDirect, CtiLMControlArea* controlArea,
                                                           const CtiTime& start, const CtiTime& stop)
{
    CtiTime startTime = start;

    lmProgramDirect->setManualControlReceivedFlag(FALSE);
    lmProgramDirect->setProgramState(CtiLMProgramBase::ScheduledState);

    lmProgramDirect->setDirectStartTime(startTime);
    lmProgramDirect->setStartedControlling(startTime);

    // Let any notification groups know if they care
    lmProgramDirect->scheduleNotification(start, stop);
    
    if( stop.seconds() < CtiTime(CtiDate(1,1,1991),0,0,0).seconds() )
    {//saves us from stopping immediately after starting if client is dumb enough to send us a stop time of 1990
        CtiTime pluggedStopTime(lmProgramDirect->getDirectStartTime());
        pluggedStopTime = pluggedStopTime.addDays(1);
        lmProgramDirect->setDirectStopTime(pluggedStopTime);
    }
    else
    {
        lmProgramDirect->setDirectStopTime(stop);
    }
    
    lmProgramDirect->setCurrentGearNumber(_controlMsg->getStartGear()-1);
    if( _controlMsg->getStartPriority() > controlArea->getCurrentStartPriority() )
    {
        controlArea->setCurrentStartPriority(_controlMsg->getStartPriority());
    }
    
    lmProgramDirect->setManualControlReceivedFlag(TRUE);
    controlArea->setUpdatedFlag(TRUE);
}

void CtiLMManualControlRequestExecutor::StopDirectProgram(CtiLMProgramDirect* lmProgramDirect, CtiLMControlArea* controlArea, const CtiTime& stop)
{
    CtiTime stopTime;
    if(_controlMsg->getCommand() == CtiLMManualControlRequest::SCHEDULED_STOP)
    {
        stopTime = stop;
    }

    // Check the stop time to see if it is before the start time
    if(stopTime.seconds() < lmProgramDirect->getDirectStartTime().seconds())
    {
        // If we have already notified of start then we need to notify of stop.
        if( (lmProgramDirect->getDirectStartTime().seconds() - lmProgramDirect->getNotifyActiveOffset()) < CtiTime::now().seconds())
        {
            lmProgramDirect->scheduleStopNotification(stopTime);
        }
        else
        {
            lmProgramDirect->setNotifyInactiveTime(gInvalidCtiTime);
        }

        lmProgramDirect->setNotifyActiveTime(gInvalidCtiTime);          
        lmProgramDirect->setDirectStartTime(gInvalidCtiTime);
        lmProgramDirect->setDirectStopTime(gInvalidCtiTime);    
        lmProgramDirect->setProgramState(CtiLMProgramBase::InactiveState);
        controlArea->setUpdatedFlag(TRUE);
    }
    else
    {
        lmProgramDirect->setManualControlReceivedFlag(FALSE);
        lmProgramDirect->setDirectStopTime(stopTime);

        lmProgramDirect->scheduleStopNotification(stopTime);
        
        lmProgramDirect->setManualControlReceivedFlag(TRUE);
        controlArea->setUpdatedFlag(TRUE);
    }
}

void CtiLMManualControlRequestExecutor::StartCurtailmentProgram(CtiLMProgramCurtailment* lmProgramCurtailment, CtiLMControlArea* controlArea,
                                                                const CtiTime& start, const CtiTime& stop)
{
    CtiTime startTime = start;
    CtiTime notificationTime;
    if(_controlMsg->getCommand() == CtiLMManualControlRequest::SCHEDULED_START)
    {
        notificationTime = _controlMsg->getNotifyTime();
    }
    lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
    lmProgramCurtailment->setProgramState(CtiLMProgramBase::ScheduledState);
    lmProgramCurtailment->setCurtailReferenceId(0);// This forces the program to create a new ref id
    lmProgramCurtailment->setActionDateTime(CtiTime());
    lmProgramCurtailment->setNotificationDateTime(notificationTime);
    lmProgramCurtailment->setCurtailmentStartTime(startTime);
    lmProgramCurtailment->setStartedControlling(startTime);
    lmProgramCurtailment->setCurtailmentStopTime(stop);
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
    controlArea->setUpdatedFlag(TRUE);
}

void CtiLMManualControlRequestExecutor::StopCurtailmentProgram(CtiLMProgramCurtailment* lmProgramCurtailment, CtiLMControlArea* controlArea,
                                                               const CtiTime& stop)
{
    CtiTime stopTime;
    if(_controlMsg->getCommand() == CtiLMManualControlRequest::SCHEDULED_STOP)
    {
        stopTime = stop;
    }
    lmProgramCurtailment->setManualControlReceivedFlag(FALSE);
    lmProgramCurtailment->setCurtailmentStopTime(stop);

    if( _controlMsg->getAdditionalInfo().length() > 0 )
    {
        lmProgramCurtailment->setAdditionalInfo(_controlMsg->getAdditionalInfo());
    }
    else if( lmProgramCurtailment->getAdditionalInfo().length() == 0 )
    {
        lmProgramCurtailment->setAdditionalInfo("none");
    }
    lmProgramCurtailment->setManualControlReceivedFlag(TRUE);
    controlArea->setUpdatedFlag(TRUE);    
}

/*
 * Change start and stop so that they fit inside one of the program control windows.  Prefer an ealier control window
 * to a later one
 */
void CtiLMManualControlRequestExecutor::CoerceStartStopTime(CtiLMProgramBase* program, CtiTime& start, CtiTime& stop)
{
    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " - before coerce start: " << start.asString() << " stop: " << stop.asString() << endl;
    }
    LONG startSecondsFromBeginningOfDay = (start.hour() * 3600) + (start.minute() * 60) + start.second();
    LONG stopSecondsFromBeginningOfDay = (stop.hour() * 3600) + (stop.minute() * 60) + start.second();
    
    RWOrdered& control_windows = program->getLMProgramControlWindows();
    if(control_windows.entries() == 0)
    {   // no control windows, nothing to do
        return;
    }

    bool found_cw = false;
    for(int i = 0; i < control_windows.entries() && !found_cw; i++)
    {
        // Do the start, stop times fit into this control window?  if not maybe the next one 
        CtiLMProgramControlWindow* cw = (CtiLMProgramControlWindow*) control_windows[i];
        if(startSecondsFromBeginningOfDay <= cw->getAvailableStopTime())
        {
            //Lets pick this control window, figure out the start and stop
            if(startSecondsFromBeginningOfDay <= cw->getAvailableStartTime())
            {   // start is before beginning of control window, set the start time to the
                // beginning of this control window
                start += (cw->getAvailableStartTime() - startSecondsFromBeginningOfDay);
                found_cw = true;
            }
            else
            {  
                // start is inside this control window, no need to change it
                found_cw = true;                
            }

            if(stopSecondsFromBeginningOfDay >= cw->getAvailableStopTime())
            {  // stop time is outside this control window, shorten it up
                stop += (-1*(stopSecondsFromBeginningOfDay - cw->getAvailableStopTime()));
            }
            else
            {
                // stop is inside this controlw window, no need to change it
            }
        }
    }

        {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " - after coerce start: " << start.asString() << " stop: " << stop.asString() << endl;
    }
}

void CoerceStopTime(CtiLMProgramBase* program, CtiTime& stop)
{

}

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeControlMsgExecutor::Execute()
{
    switch ( _energyExchangeMsg->getCommand() )
    {
    case CtiLMEnergyExchangeControlMsg::NEW_OFFER:
        NewOffer();
        break;

    case CtiLMEnergyExchangeControlMsg::OFFER_UPDATE:
        OfferUpdate();
        break;

    case CtiLMEnergyExchangeControlMsg::OFFER_REVISION:
        OfferRevision();
        break;

    case CtiLMEnergyExchangeControlMsg::CLOSE_OFFER:
        CloseOffer();
        break;

    case CtiLMEnergyExchangeControlMsg::CANCEL_OFFER:
        CancelOffer();
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - executor.cpp::Execute - unknown command type at: " << __LINE__ << endl;
        }

    }
}

/*---------------------------------------------------------------------------
    NewOffer
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeControlMsgExecutor::NewOffer()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << CtiTime() << " - NEW energy exchange offer received." << endl;
        _ltoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << "pao id: " << tempchar << endl;
        dout << "offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << "notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << "expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << "additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    LONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
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
                                newRevision->setActionDateTime(CtiTime());
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
                                for(LONG k=0;k<HOURS_IN_DAY;k++)
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
                            dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << CtiTime() << " - Energy exchange offer UPDATE received." << endl;
        _ltoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << "pao id: " << tempchar << endl;
        _ltoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << "offer id: " << tempchar << endl;
        dout << "offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << "notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << "expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << "additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    LONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
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
                                    updateRevision->setActionDateTime(CtiTime());
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
                                    for(LONG k=0;k<HOURS_IN_DAY;k++)
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
                                    dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate no offer to update for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferUpdate no offers to update in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMEnergyExchangeControlMsgExecutor::OfferRevision()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << CtiTime() << " - Energy exchange offer REVISION received." << endl;
        _ltoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << "pao id: " << tempchar << endl;
        _ltoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << "offer id: " << tempchar << endl;
        dout << "offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << "notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << "expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << "additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    LONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
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
                                    if( currentRevision->getOfferExpirationDateTime() > CtiTime() )
                                    {
                                        currentRevision->setOfferExpirationDateTime(CtiTime());
                                        currentRevision->updateLMEnergyExchangeOfferRevisionTable();
                                    }
                                    RWOrdered& offerRevisions = revisionOffer->getLMEnergyExchangeOfferRevisions();
                                    CtiLMEnergyExchangeOfferRevision* newRevision = new CtiLMEnergyExchangeOfferRevision();

                                    newRevision->setOfferId(revisionOffer->getOfferId());
                                    newRevision->setRevisionNumber( currentRevision->getRevisionNumber() + 1 );
                                    newRevision->setActionDateTime(CtiTime());
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
                                    for(LONG k=0;k<HOURS_IN_DAY;k++)
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
                                    dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferRevision no offer to revise for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::OfferRevison no offers to revise in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMEnergyExchangeControlMsgExecutor::CloseOffer()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << CtiTime() << " - Energy exchange offer CLOSE received." << endl;
        _ltoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << "pao id: " << tempchar << endl;
        _ltoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << "offer id: " << tempchar << endl;
        dout << "offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << "notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << "expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << "additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    LONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
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
                                    //closeRevision->setActionDateTime(CtiTime());
                                    //closeRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    closeRevision->setOfferExpirationDateTime(CtiTime());
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
                                    dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offer to close for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offers to close in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMEnergyExchangeControlMsgExecutor::CancelOffer()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        char tempchar[64];
        dout << CtiTime() << " - Energy exchange offer CANCEL received." << endl;
        _ltoa(_energyExchangeMsg->getPAOId(),tempchar,10);
        dout << "pao id: " << tempchar << endl;
        _ltoa(_energyExchangeMsg->getOfferId(),tempchar,10);
        dout << "offer id: " << tempchar << endl;
        dout << "offer date: " << _energyExchangeMsg->getOfferDate().asString() << endl;
        dout << "notification datetime: " << _energyExchangeMsg->getNotificationDateTime().asString() << endl;
        dout << "expiration datetime: " << _energyExchangeMsg->getExpirationDateTime().asString() << endl;
        dout << "additional info: " << _energyExchangeMsg->getAdditionalInfo() << endl;
    }

    BOOL found = FALSE;
    LONG energyExchangeProgramID = _energyExchangeMsg->getPAOId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
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
                                    //cancelRevision->setActionDateTime(CtiTime());
                                    //cancelRevision->setNotificationDateTime(_energyExchangeMsg->getNotificationDateTime());
                                    cancelRevision->setOfferExpirationDateTime(CtiTime());
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
                                    dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offer to close for given date in file: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor::CloseOffer no offers to close in file: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - CtiLMEnergyExchangeControlMsgExecutor command type and LM Program type mismatch in file: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMControlAreaMsgExecutor::Execute()
{
    CtiLMClientListener::getInstance()->BroadcastMessage(((CtiMessage*)_controlAreaMsg));
}


/*===========================================================================
    CtiLMCurtailmentAcknowledgeMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMCurtailmentAcknowledgeMsgExecutor::Execute()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Curtail acknowledge received." << endl;

        char tempchar[64];
        _ltoa(_curtailAckMsg->getPAOId(),tempchar,10);
        string outString = tempchar;
        dout << "pao id: " << outString.c_str() << endl;
        _ltoa(_curtailAckMsg->getCurtailReferenceId(),tempchar,10);
        outString = tempchar;
        dout << "curtail reference id: " << outString.c_str() << endl;
        dout << "acknowledge status: " << _curtailAckMsg->getAcknowledgeStatus() << endl;
        dout << "ip address of ack user: " << _curtailAckMsg->getIPAddressOfAckUser() << endl;
        dout << "user id name: " << _curtailAckMsg->getUserIdName() << endl;
        dout << "name of ack person: " << _curtailAckMsg->getNameOfAckPerson() << endl;
        dout << "curtailmentnotes: " << _curtailAckMsg->getCurtailmentNotes() << endl;
    }

    CtiTime currentDateTime;
    LONG curtailmentCustomerID = _curtailAckMsg->getPAOId();
    LONG curtailReferenceID = _curtailAckMsg->getCurtailReferenceId();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    if( controlAreas.entries() > 0 )
    {
        BOOL found = FALSE;
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;
                        RWOrdered& lmCurtailmentCustomers = lmProgramCurtailment->getLMProgramCurtailmentCustomers();
                        if( lmCurtailmentCustomers.entries() > 0 )
                        {
                            for(LONG k=0;k<lmCurtailmentCustomers.entries();k++)
                            {
                                CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailmentCustomers[k];

                                if( (curtailmentCustomerID == currentLMCurtailCustomer->getCustomerId()) &&
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
                                    currentLMCurtailCustomer->dumpDynamicData();
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


void figureHourlyCommittedForOfferId(LONG offerId, const RWOrdered& lmEnergyExchangeCustomers, DOUBLE committedArray[ ])
{
    for(LONG i=0;i<lmEnergyExchangeCustomers.entries();i++)
    {
        CtiLMEnergyExchangeCustomer* currentEECustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[i];
        RWOrdered& customerReplies = currentEECustomer->getLMEnergyExchangeCustomerReplies();
        for(LONG j=0;j<customerReplies.entries();j++)
        {
            CtiLMEnergyExchangeCustomerReply* currentLMEECustomerReply = (CtiLMEnergyExchangeCustomerReply*)customerReplies[j];
            if( currentLMEECustomerReply->getOfferId() == offerId &&
                !stringCompareIgnoreCase(currentLMEECustomerReply->getAcceptStatus(), CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus) )
            {
                RWOrdered& hourlyCustomers = currentLMEECustomerReply->getLMEnergyExchangeHourlyCustomers();

                for(LONG k=0;k<hourlyCustomers.entries();k++)
                {
                    committedArray[k] += ((CtiLMEnergyExchangeHourlyCustomer*)hourlyCustomers[k])->getAmountCommitted();
                }
            }
        }
    }
}

/*===========================================================================
    CtiLMEnergyExchangeAcceptMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMEnergyExchangeAcceptMsgExecutor::Execute()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Energy exchange accept received." << endl;

        char tempchar[64];
        _ltoa(_energyExchangeAcceptMsg->getPAOId(),tempchar,10);
        string outString = tempchar;
        dout << "pao id: " << outString.c_str() << endl;
        _ltoa(_energyExchangeAcceptMsg->getOfferId(),tempchar,10);
        outString = tempchar;
        dout << "offer id: " << outString.c_str() << endl;
        _ltoa(_energyExchangeAcceptMsg->getRevisionNumber(),tempchar,10);
        outString = tempchar;
        dout << "revision number: " << outString.c_str() << endl;
        dout << "accept status: " << _energyExchangeAcceptMsg->getAcceptStatus() << endl;
        dout << "ip address of accept user: " << _energyExchangeAcceptMsg->getIPAddressOfAcceptUser() << endl;
        dout << "user id name: " << _energyExchangeAcceptMsg->getUserIdName() << endl;
        dout << "name of accept person: " << _energyExchangeAcceptMsg->getNameOfAcceptPerson() << endl;
        dout << "energy exchange notes: " << _energyExchangeAcceptMsg->getEnergyExchangeNotes() << endl;
    }

    CtiTime currentDateTime;
    LONG customerID = _energyExchangeAcceptMsg->getPAOId();
    LONG offerID = _energyExchangeAcceptMsg->getOfferId();
    LONG revisionNumber = _energyExchangeAcceptMsg->getRevisionNumber();
    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    RWOrdered& controlAreas = *store->getControlAreas(CtiTime().seconds());

    LONG numberOfHoursOverCommitted = 0;
    LONG overCommittedArray[HOURS_IN_DAY];

    if( controlAreas.entries() > 0 )
    {
        BOOL found = FALSE;
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                    {
                        CtiLMProgramEnergyExchange* lmProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;
                        RWOrdered& lmEnergyExchangeCustomers = lmProgramEnergyExchange->getLMEnergyExchangeCustomers();
                        if( lmEnergyExchangeCustomers.entries() > 0 )
                        {
                            for(LONG k=0;k<lmEnergyExchangeCustomers.entries();k++)
                            {
                                CtiLMEnergyExchangeCustomer* currentLMEnergyExchangeCustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[k];

                                if( customerID == currentLMEnergyExchangeCustomer->getCustomerId() )
                                {
                                    DOUBLE committedArray[HOURS_IN_DAY] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
                                    figureHourlyCommittedForOfferId(offerID, lmEnergyExchangeCustomers, committedArray);

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
                                                CtiLMEnergyExchangeOffer* currentOffer = lmProgramEnergyExchange->getOfferWithId(offerID);
                                                CtiLMEnergyExchangeOfferRevision* currentRevision = currentOffer->getCurrentOfferRevision();
                                                RWOrdered& revisionHourlyOffers = currentRevision->getLMEnergyExchangeHourlyOffers();

                                                if( !stringCompareIgnoreCase(currentLMEnergyExchangeCustomerReply->getAcceptStatus(), CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus) )
                                                {
                                                    if( lmHourlyCustomers.entries() == 0 )
                                                    {
                                                        currentLMEnergyExchangeCustomerReply->updateLMEnergyExchangeCustomerReplyTable();
                                                        for(LONG m=0;m<HOURS_IN_DAY;m++)
                                                        {
                                                            CtiLMEnergyExchangeHourlyCustomer* newHourlyCustomer = new CtiLMEnergyExchangeHourlyCustomer();
                                                            newHourlyCustomer->setCustomerId(customerID);
                                                            newHourlyCustomer->setOfferId(offerID);
                                                            newHourlyCustomer->setRevisionNumber(revisionNumber);
                                                            newHourlyCustomer->setHour(m);
                                                            newHourlyCustomer->setAmountCommitted(_energyExchangeAcceptMsg->getAmountCommitted(m));
                                                            newHourlyCustomer->addLMEnergyExchangeHourlyCustomerTable();
                                                            lmHourlyCustomers.insert(newHourlyCustomer);

                                                            DOUBLE amountRequested = ((CtiLMEnergyExchangeHourlyOffer*)revisionHourlyOffers[m])->getAmountRequested();
                                                            DOUBLE oldAmountCommitted = committedArray[m];
                                                            DOUBLE newAmountCommitted = newHourlyCustomer->getAmountCommitted();
                                                            if( amountRequested > 0.0 &&
                                                                (oldAmountCommitted + newAmountCommitted) >= amountRequested )
                                                            {
                                                                overCommittedArray[numberOfHoursOverCommitted] = m;
                                                                numberOfHoursOverCommitted++;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Accept for offer revision that already contains hourly commitments, in: " << __FILE__ << " at: " << __LINE__ << endl;
                                                    }
                                                }
                                                //currentLMCurtailCustomer->dumpDynamicData();

                                                if( numberOfHoursOverCommitted > 0 )
                                                {//this block is to close overcommitted hours if there are any
                                                    //create a new revision
                                                    lmProgramEnergyExchange->setManualControlReceivedFlag(FALSE);

                                                    RWOrdered& offerRevisions = currentOffer->getLMEnergyExchangeOfferRevisions();
                                                    CtiLMEnergyExchangeOfferRevision* newRevision = new CtiLMEnergyExchangeOfferRevision();

                                                    newRevision->setOfferId(currentOffer->getOfferId());
                                                    newRevision->setRevisionNumber( currentRevision->getRevisionNumber() + 1 );
                                                    newRevision->setActionDateTime(CtiTime());
                                                    newRevision->setNotificationDateTime(CtiTime());
                                                    newRevision->setOfferExpirationDateTime(currentRevision->getOfferExpirationDateTime());
                                                    newRevision->setAdditionalInfo("Offer was revised automatically due to the over committed hours.");
                                                    if( currentRevision->getOfferExpirationDateTime() > CtiTime() )
                                                    {
                                                        currentRevision->setOfferExpirationDateTime(CtiTime());
                                                        currentRevision->updateLMEnergyExchangeOfferRevisionTable();
                                                    }

                                                    newRevision->addLMEnergyExchangeOfferRevisionTable();
                                                    offerRevisions.insert(newRevision);

                                                    RWOrdered& newHourlyOffers = newRevision->getLMEnergyExchangeHourlyOffers();
                                                    LONG currentOverCommittedHourPosition = 0;
                                                    for(LONG y=0;y<HOURS_IN_DAY;y++)
                                                    {
                                                        CtiLMEnergyExchangeHourlyOffer* newHourlyOffer = new CtiLMEnergyExchangeHourlyOffer();
                                                        newHourlyOffer->setOfferId(newRevision->getOfferId());
                                                        newHourlyOffer->setRevisionNumber(newRevision->getRevisionNumber());
                                                        newHourlyOffer->setHour(y);
                                                        if( currentOverCommittedHourPosition < numberOfHoursOverCommitted &&
                                                            overCommittedArray[currentOverCommittedHourPosition] == y )
                                                        {//zero out over committed hour
                                                            newHourlyOffer->setPrice(0.0);
                                                            newHourlyOffer->setAmountRequested(0.0);
                                                            currentOverCommittedHourPosition++;
                                                        }
                                                        else
                                                        {
                                                            newHourlyOffer->setPrice(((CtiLMEnergyExchangeHourlyOffer*)revisionHourlyOffers[y])->getPrice());
                                                            newHourlyOffer->setAmountRequested(((CtiLMEnergyExchangeHourlyOffer*)revisionHourlyOffers[y])->getAmountRequested());
                                                        }
                                                        newHourlyOffer->addLMEnergyExchangeHourlyOfferTable();
                                                        newHourlyOffers.insert(newHourlyOffer);
                                                    }

                                                    //lmProgramEnergyExchange->addLMCurtailProgramActivityTable();
                                                    //lmProgramEnergyExchange->dumpDynamicData();
                                                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::ScheduledRunStatus);
                                                    lmProgramEnergyExchange->setManualControlReceivedFlag(TRUE);
                                                }//this block is to close overcommitted hours if there are any
                                                currentControlArea->setUpdatedFlag(TRUE);
                                            }
                                            else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " - Accept for offer revision that has expired, in: " << __FILE__ << " at: " << __LINE__ << endl;
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
void CtiLMMultiMsgExecutor::Execute()
{
    CtiLMExecutorFactory f;
    RWOrdered& messages = _multiMsg->getData();
    while(messages.entries( )>0)
    {
        CtiMessage* message = (CtiMessage*)(messages.pop());
        if( message != NULL )
        {
            CtiLMExecutor* executor = f.createExecutor(message);
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
    CtiLMForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiLMForwardMsgToDispatchExecutor::Execute()
{
    CtiLoadManager::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
}


/*===========================================================================
    CtiLMShutdown
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes a shutdown on the server
    THIS EXECUTOR IS THE EXCEPTION
    IT MUST NOT BE EXECUTED ON THE MAIN THREAD AS THE REST OF THEM SHOULD BE
    THE REASON IS BECAUSE IT SHUTS DOWN THE CTILOADMANAGER WHICH OWNS
    THE MAIN THREAD
---------------------------------------------------------------------------*/
void CtiLMShutdownExecutor::Execute()
{
    try
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        CtiLMClientListener::getInstance()->stop();

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
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
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down load manager thread..." << endl;
        }
    
        CtiLoadManager::getInstance()->stop();

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Load manager thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down control area store..." << endl;
        }
    
        CtiLMControlAreaStore::deleteInstance();

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Control area store shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*===========================================================================
    CtiLMExecutorFactory
===========================================================================*/
CtiLMExecutor* CtiLMExecutorFactory::createExecutor(const CtiMessage* message)
{
    CtiLMExecutor* ret_val = 0;
    CtiServerRequestMsg* request = 0;
    LONG classId = message->isA();

//     A message could come in wrapped in a server request message.
    if( classId == MSG_SERVER_REQUEST )
    {
        request = (CtiServerRequestMsg*) message;
        message = (CtiMessage*) request->getPayload();
        if( message  == NULL )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " **Checkpoint** " << "CtiLMServerRequest received but it contains no payload, doing nothing!" << __FILE__ << "(" << __LINE__ << ")" << endl;
            return 0;
        }
        classId = message->isA();
    }

    switch ( classId )
    {
        case CTILMCOMMAND_ID:
            ret_val = new CtiLMCommandExecutor( (CtiLMCommand*)message );
            break;
    
        case CTILMCONTROLAREA_MSG_ID:
            ret_val = new CtiLMControlAreaMsgExecutor( (CtiLMControlAreaMsg*)message );
            break;
    
        case CTILMMANUALCONTROLREQUEST_ID:
            ret_val = new CtiLMManualControlRequestExecutor( (CtiLMManualControlRequest*)message, request );
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
                dout << CtiTime() << " - CtiLMExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            }
    }

    return ret_val;
}
