/*---------------------------------------------------------------------------
        Filename:  lmcontrolarea.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMControlArea.
                        CtiLMControlArea maintains the state and handles
                        the persistence of control areas for Load
                        Management.

        Initial Date:  2/12/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmcontrolarea.h"
#include "lmcontrolareatrigger.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramdirect.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "devicetypes.h"
#include "device.h"
#include "loadmanager.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMControlArea, CTILMCONTROLAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMControlArea::CtiLMControlArea()
{
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Default Constructor, Number of CtiLMControlArea increased to: " << numberOfReferences << endl;
    }*/
}

CtiLMControlArea::CtiLMControlArea(RWDBReader& rdr)
{
    restore(rdr);
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Restore Constructor, Number of CtiLMControlArea increased to: " << numberOfReferences << endl;
    }*/
}

CtiLMControlArea::CtiLMControlArea(const CtiLMControlArea& controlarea)
{
    operator=(controlarea);
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Copy Constructor, Number of CtiLMControlArea increased to: " << numberOfReferences << endl;
    }*/
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMControlArea::~CtiLMControlArea()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    _lmcontrolareatriggers.clearAndDestroy();
    _lmprograms.clearAndDestroy();
    /*numberOfReferences--;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Destructor, Number of CtiLMControlArea decreased to: " << numberOfReferences << endl;
    }*/
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the control area
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the control area
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getDefOperationalState

    Returns the default operational state of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getDefOperationalState() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _defoperationalstate;
}

/*---------------------------------------------------------------------------
    getControlInterval

    Returns the control interval of the control area in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getControlInterval() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlinterval;
}

/*---------------------------------------------------------------------------
    getMinResponseTime

    Returns the minimum response time of the control area in seconds
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getMinResponseTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _minresponsetime;
}

/*---------------------------------------------------------------------------
    getDefDailyStartTime

    Returns the default daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefDailyStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _defdailystarttime;
}

/*---------------------------------------------------------------------------
    getDefDailyStopTime

    Returns the default daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefDailyStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _defdailystoptime;
}

/*---------------------------------------------------------------------------
    getRequireAllTriggersActiveFlag

    Returns the require all triggers active flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getRequireAllTriggersActiveFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _requirealltriggersactiveflag;
}

/*---------------------------------------------------------------------------
    getNextCheckTime

    Returns the next check time of the control area
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMControlArea::getNextCheckTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getLMControlAreaTriggers

    Returns the list of triggers for this control area
---------------------------------------------------------------------------*/
RWOrdered& CtiLMControlArea::getLMControlAreaTriggers()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmcontrolareatriggers;
}

/*---------------------------------------------------------------------------
    getLMPrograms

    Returns the list of programs for this control area
---------------------------------------------------------------------------*/
RWOrdered& CtiLMControlArea::getLMPrograms()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmprograms;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getNewPointDataReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getUpdatedFlag

    Returns the updated flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getUpdatedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _updatedflag;
}

/*---------------------------------------------------------------------------
    getControlAreaStatusPointId

    Returns the point id of the control area status point
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getControlAreaStatusPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlareastatuspointid;
}

/*---------------------------------------------------------------------------
    getControlAreaState

    Returns the state of the control area
---------------------------------------------------------------------------*/
ULONG CtiLMControlArea::getControlAreaState() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlareastate;
}

/*---------------------------------------------------------------------------
    getCurrentPriority

    Returns the current priority of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentPriority() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentpriority;
}

/*---------------------------------------------------------------------------
    getCurrentDailyStartTime

    Returns the current daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentDailyStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentdailystarttime;
}

/*---------------------------------------------------------------------------
    getCurrentDailyStopTime

    Returns the default daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentDailyStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentdailystoptime;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the pao id of the control area - use with caution
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOCategory(const RWCString& category)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOType(ULONG type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefOperationalState

    Sets the default operational state of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDefOperationalState(const RWCString& opstate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _defoperationalstate = opstate;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the control area in seconds
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlInterval(ULONG interval)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlinterval = interval;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinResponseTime

    Sets the minimum response time of the control area in seconds
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setMinResponseTime(ULONG response)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _minresponsetime = response;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefDailyStartTime

    Sets the default daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDefDailyStartTime(LONG start)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _defdailystarttime = start;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefDailyStopTime

    Sets the default daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDefDailyStopTime(LONG stop)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _defdailystoptime = stop;

    return *this;
}

/*---------------------------------------------------------------------------
    setRequireAllTriggersActiveFlag

    Sets the require all triggers active flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setRequireAllTriggersActiveFlag(BOOL requireall)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _requirealltriggersactiveflag = requireall;

    return *this;
}

/*---------------------------------------------------------------------------
    figureNextCheckTime

    Figures out when the control area should be checked again according to the
    control interval.
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::figureNextCheckTime(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _controlinterval != 0 )
    {
        ULONG tempsum = (secondsFrom1901-(secondsFrom1901%_controlinterval))+_controlinterval;
        _nextchecktime = RWDBDateTime(RWTime(tempsum));
    }
    else
    {
        _nextchecktime = RWDBDateTime();
    }

    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setNewPointDataReceivedFlag(BOOL newdatareceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _newpointdatareceivedflag = newdatareceived;

    return *this;
}

/*---------------------------------------------------------------------------
    setUpdatedFlag

    Sets the updated flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setUpdatedFlag(BOOL updated)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _updatedflag = updated;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlAreaStatusPointId

    Sets the point id of the control area status point
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlAreaStatusPointId(ULONG statuspointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlareastatuspointid = statuspointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlAreaState

    Sets the state of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlAreaState(ULONG state)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlareastate = state;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentPriority

    Sets the current priority of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentPriority(LONG currpriority)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentpriority = currpriority;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyStartTime

    Sets the current daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentDailyStartTime(LONG tempstart)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentdailystarttime = tempstart;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyStopTime

    Sets the current daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentDailyStopTime(LONG tempstop)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentdailystoptime = tempstop;

    return *this;
}


/*---------------------------------------------------------------------------
    isControlTime

    Returns a BOOLean if the control area can be controlled at the current
    time and day of the week.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isControlTime(ULONG secondsFromBeginningOfDay)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    LONG tempCurrentDailyStartTime = getCurrentDailyStartTime();
    LONG tempCurrentDailyStopTime = getCurrentDailyStopTime();

    if( getCurrentDailyStartTime() == -1 )
    {
        tempCurrentDailyStartTime = 0;
    }
    if( getCurrentDailyStopTime() == -1 )
    {
        tempCurrentDailyStopTime = 86400;
    }

    if( tempCurrentDailyStartTime <= secondsFromBeginningOfDay && secondsFromBeginningOfDay <= tempCurrentDailyStopTime )
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

/*---------------------------------------------------------------------------
    isControlStillNeeded

    Returns a BOOLean if the control area can stop all control because the
    load + reduction is below the threshold - minrestoreoffset or a status
    trigger has returned to it's normal state.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isControlStillNeeded()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = TRUE;

    DOUBLE currentReduction = 0.0;
    if( _lmprograms.entries() > 0 )
    {
        for(ULONG i=0;i<_lmprograms.entries();i++)
        {
            CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
            if( currentLMProgram->getReductionTotal() > 0.0 )
            {
                currentReduction += currentLMProgram->getReductionTotal();
            }
        }
    }

    if( _lmcontrolareatriggers.entries() > 0 )
    {
        UINT triggersStillTripped = 0;
        for(UINT i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType )
            {
                if( ( (currentTrigger->getPointValue() + currentReduction) >
                      (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) ||
                    ( (currentTrigger->getProjectedPointValue() + currentReduction) >
                      (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) )
                {
                    triggersStillTripped++;
                }
            }
            else if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::StatusTriggerType )
            {
                if( currentTrigger->getPointValue() != currentTrigger->getNormalState() )
                {
                    triggersStillTripped++;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unknown Trigger Type in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }

        if( triggersStillTripped == 0 )
        {
            returnBoolean = FALSE;
        }
        else if( getRequireAllTriggersActiveFlag() && ( triggersStillTripped < _lmcontrolareatriggers.entries() ) )
        {
            returnBoolean = FALSE;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isPastMinResponseTime

    Returns a BOOLean if the control area can be controlled more because the
    time since the last control is at least as long as the min response time.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isPastMinResponseTime(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBDateTime currentDateTime;
    BOOL returnBoolean = TRUE;

    if( _lmprograms.entries() > 0 )
    {
        for(ULONG i=0;i<_lmprograms.entries();i++)
        {
            CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
            if( currentLMProgram->getLastControlSent().seconds() + getMinResponseTime() > secondsFrom1901 )
            {
                returnBoolean = FALSE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isManualControlReceived

    Returns a BOOLean if one or more of the programs in the control area
    have received a manual control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isManualControlReceived()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;

    if( _lmprograms.entries() > 0 )
    {
        for(ULONG i=0;i<_lmprograms.entries();i++)
        {
            CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
            if( currentLMProgram->getManualControlReceivedFlag() )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isThresholdTriggerTripped

    Returns a BOOLean if any threshold trigger is tripped for the control area.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isThresholdTriggerTripped()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL returnBoolean = FALSE;
    if( _lmcontrolareatriggers.entries() > 0 )
    {
        for(UINT i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType )
            {
                if( currentTrigger->getPointValue() > currentTrigger->getThreshold() ||
                    currentTrigger->getProjectedPointValue() > currentTrigger->getThreshold() )
                {
                    returnBoolean = TRUE;
                    break;
                }
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    calculateLoadReductionNeeded

    Returns a double value representing the amount of KW we need to reduce
    in order to bring the control area to an acceptable amount of load.
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::calculateLoadReductionNeeded()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    DOUBLE returnLoadReductionNeeded = 0.0;
    if( _lmcontrolareatriggers.entries() > 0 )
    {
        UINT triggersTripped = 0;
        for(UINT i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType )
            {
                if( currentTrigger->getPointValue() > currentTrigger->getThreshold() ||
                    currentTrigger->getProjectedPointValue() > currentTrigger->getThreshold() )
                {
                    if( (currentTrigger->getPointValue() - currentTrigger->getThreshold()) >
                        (currentTrigger->getProjectedPointValue() - currentTrigger->getThreshold()) )
                    {
                        returnLoadReductionNeeded += currentTrigger->getPointValue() - currentTrigger->getThreshold();
                    }
                    else
                    {
                        returnLoadReductionNeeded += currentTrigger->getProjectedPointValue() - currentTrigger->getThreshold();
                    }

                    triggersTripped++;

                    if( currentTrigger->getThresholdKickPercent() > 0 )
                    {
                        DOUBLE oldThreshold = currentTrigger->getThreshold();
                        DOUBLE thresholdKickDouble = ((DOUBLE)currentTrigger->getThresholdKickPercent())/100.0;
                        ULONG thresholdKickOffset = (ULONG)(currentTrigger->getThreshold() * thresholdKickDouble);
                        ULONG amountOverKickValue = currentTrigger->getPointValue() - currentTrigger->getThreshold() - thresholdKickOffset;
                        if( amountOverKickValue > 0 )
                        {
                            currentTrigger->setThreshold( currentTrigger->getThreshold() + amountOverKickValue );
                            CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(this, currentTrigger);
                            setUpdatedFlag(TRUE);
                            {
                                char tempchar[80] = "";
                                RWCString text = RWCString("Automatic Threshold Kick Up");
                                RWCString additional = RWCString("Threshold for Trigger: ");
                                _snprintf(tempchar,80,"%d",currentTrigger->getTriggerNumber());
                                additional += tempchar;
                                additional += " changed in LMControlArea: ";
                                additional += getPAOName();
                                additional += " PAO ID: ";
                                _snprintf(tempchar,80,"%d",getPAOId());
                                additional += tempchar;
                                additional += " old threshold: ";
                                _snprintf(tempchar,80,"%.*f",3,oldThreshold);
                                additional += tempchar;
                                additional += " new threshold: ";
                                _snprintf(tempchar,80,"%.*f",3,currentTrigger->getThreshold());
                                additional += tempchar;
                                additional += " point value was: ";
                                _snprintf(tempchar,80,"%.*f",3,currentTrigger->getPointValue());
                                additional += tempchar;
                                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                                if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << text << ", " << additional << endl;
                                }
                            }
                        }
                    }
                }
            }
            else if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::StatusTriggerType )
            {
                if( currentTrigger->getPointValue() != currentTrigger->getNormalState() )
                {
                    if( returnLoadReductionNeeded == 0.0 )
                    {
                        returnLoadReductionNeeded = 0.1;
                    }
                    triggersTripped++;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unknown Trigger Type in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }

        if( getRequireAllTriggersActiveFlag() && ( triggersTripped == _lmcontrolareatriggers.entries() ) )
        {
            returnLoadReductionNeeded = 0.0;
        }
    }

    return returnLoadReductionNeeded;
}

/*---------------------------------------------------------------------------
    reduceControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::reduceControlAreaLoad(DOUBLE loadReductionNeeded, ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    DOUBLE expectedLoadReduced = 0.0;
    ULONG newlyActivePrograms = 0;
    ULONG fullyActivePrograms = 0;

    for(ULONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( !currentLMProgram->getDisableFlag() &&
            (currentLMProgram->getControlType() == CtiLMProgramBase::AutomaticType || currentLMProgram->getControlType() == "Enabled") )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( currentLMProgram->isAvailableToday() && currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() )
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    RWCString text = RWCString("Start, LM Control Area: ");
                    text += getPAOName();
                    RWCString additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                }

                if( currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState )
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgram;
                        if( currentLMProgram->getDefaultPriority() > getCurrentPriority() )
                        {
                            if( getCurrentPriority() < 0 ||
                                newlyActivePrograms == 0 )
                            {//is inactive or current pass hasn't controlled any new programs
                                setCurrentPriority(currentLMProgram->getDefaultPriority());
                            }
                            else
                            {
                                //load has already been reduced in one priority in this
                                //control area, we only add control to one priority at a
                                //time
                                break;
                            }
                        }

                        expectedLoadReduced = lmProgramDirect->reduceProgramLoad(loadReductionNeeded, getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg);
                        newlyActivePrograms++;
                        if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                            getControlAreaState() != CtiLMControlArea::ActiveState )
                        {
                            setControlAreaState(CtiLMControlArea::ActiveState);
                        }
                    }
                    else if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Load Management can not automatically manage curtailment programs yet. in: " << __FILE__ << " at:" << __LINE__ << endl;
                        /*CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgram;
                        expectedLoadReduced = lmProgramCurtailment->reduceProgramLoad(loadReductionNeeded, multiPilMsg);
                        if( currentLMProgram->getDefaultPriority() > getCurrentPriority() )
                        {
                            setCurrentPriority(currentLMProgram->getDefaultPriority());
                        }
                        if( expectedLoadReduced > 0.0 )
                        {
                            break;
                        }*/
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown LM Program Type in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else
                {
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }*/
                }
            }
        }
        else if( currentLMProgram->getControlType() != CtiLMProgramBase::ManualOnlyType )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unknown LM Program Control Type: " << currentLMProgram->getControlType() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    for(ULONG j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState )
        {
            fullyActivePrograms++;
        }
        else
        {
            break;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    takeAllAvailableControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::takeAllAvailableControlAreaLoad(ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    DOUBLE expectedLoadReduced = 0.0;

    for(ULONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( currentLMProgram->getControlType() == CtiLMProgramBase::AutomaticType ||
            currentLMProgram->getControlType() == "Enabled" )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( currentLMProgram->isAvailableToday() && currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() && !currentLMProgram->getDisableFlag() )
            {
                if( currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState )
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgram;
                        while( lmProgramDirect->getProgramState() != CtiLMProgramBase::FullyActiveState )
                        {
                            expectedLoadReduced += lmProgramDirect->reduceProgramLoad(0.0, getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg);
                        }
                        if( currentLMProgram->getDefaultPriority() > getCurrentPriority() )
                        {
                            setCurrentPriority(currentLMProgram->getDefaultPriority());
                        }
                    }
                    else if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Load Management can not automatically manage curtailment programs yet. in: " << __FILE__ << " at:" << __LINE__ << endl;
                        /*CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgram;
                        expectedLoadReduced = lmProgramCurtailment->reduceProgramLoad(loadReductionNeeded, multiPilMsg);
                        if( currentLMProgram->getDefaultPriority() > getCurrentPriority() )
                        {
                            setCurrentPriority(currentLMProgram->getDefaultPriority());
                        }
                        if( expectedLoadReduced > 0.0 )
                        {
                            break;
                        }*/
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown LM Program Type in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                else
                {
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }*/
                }
            }
        }
        else if( currentLMProgram->getControlType() != CtiLMProgramBase::ManualOnlyType )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unknown LM Program Control Type in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    setControlAreaState(CtiLMControlArea::FullyActiveState);

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    maintainCurrentControl

    Checks all active programs to make sure that the current gear doesn't
    need to increased.  Refreshes time refresh programs shed times.  Updates
    current hours values in programs as they continue to control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::maintainCurrentControl(ULONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    BOOL returnBoolean = FALSE;
    for(ULONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            (currentLMProgram->getControlType() == CtiLMProgramBase::AutomaticType || currentLMProgram->getControlType() == "Enabled") &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( ((CtiLMProgramDirect*)currentLMProgram)->maintainProgramControl(getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg) )
            {
                returnBoolean = TRUE;
            }
        }
    }
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    stopAllControl

    Stops all Programs that are controlling in a control area, normally
    because we have left a control window.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    BOOL returnBOOL = FALSE;
    bool sentSignalMsg = false;

    ULONG previousControlAreaState = getControlAreaState();
    ULONG numberOfActivePrograms = 0;
    ULONG numberOfFullyActivePrograms = 0;
    for(ULONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            (currentLMProgram->getControlType() == CtiLMProgramBase::AutomaticType || currentLMProgram->getControlType() == "Enabled") &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( !sentSignalMsg )
            {
                RWCString text = RWCString("Automatic Stop, LM Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("");//someday we can say why we auto stopped
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
                sentSignalMsg = true;
            }
            ((CtiLMProgramDirect*)currentLMProgram)->stopProgramControl(multiPilMsg, multiDispatchMsg);
            returnBOOL = TRUE;
        }
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState )
        {
            numberOfFullyActivePrograms++;
            numberOfActivePrograms++;
        }
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            numberOfActivePrograms++;
        }
    }
    if( returnBOOL )
    {
        if( numberOfActivePrograms == 0 )
        {
            setControlAreaState(CtiLMControlArea::InactiveState);
            setCurrentPriority(-1);
        }
        else if( numberOfFullyActivePrograms > 0 &&
                 numberOfFullyActivePrograms == _lmprograms.entries() )
        {
            setControlAreaState(CtiLMControlArea::FullyActiveState);
        }
        else
        {
            setControlAreaState(CtiLMControlArea::ActiveState);
        }
    }

    return returnBOOL;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Takes appropriate action for a manual control messages.
---------------------------------------------------------------------------*/
void CtiLMControlArea::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    ULONG previousControlAreaState = getControlAreaState();
    ULONG numberOfActivePrograms = 0;
    ULONG numberOfFullyActivePrograms = 0;
    for(ULONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getManualControlReceivedFlag() )
        {
            if( currentLMProgram->handleManualControl(secondsFrom1901,multiPilMsg,multiDispatchMsg) )
            {
                setUpdatedFlag(TRUE);
            }
        }
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState )
        {
            numberOfFullyActivePrograms++;
            numberOfActivePrograms++;
        }
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            numberOfActivePrograms++;
        }
    }

    if( numberOfFullyActivePrograms > 0 &&
        numberOfFullyActivePrograms == _lmprograms.entries() )
    {
        if( getControlAreaState() != CtiLMControlArea::FullyActiveState )
        {
            setControlAreaState(CtiLMControlArea::FullyActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                RWCString text = RWCString("Manual Start, LM Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setUpdatedFlag(TRUE);
        }
    }
    else if( numberOfActivePrograms == 0 )
    {
        setControlAreaState(CtiLMControlArea::InactiveState);
        if( previousControlAreaState != CtiLMControlArea::InactiveState )
        {
            RWCString text = RWCString("Manual Stop, LMControl Area: ");
            text += getPAOName();
            RWCString additional = RWCString("");
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);
            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
        }
        setUpdatedFlag(TRUE);
    }
    else
    {
        if( getControlAreaState() != CtiLMControlArea::ActiveState )
        {
            setControlAreaState(CtiLMControlArea::ActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                RWCString text = RWCString("Manual Start, LM Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setUpdatedFlag(TRUE);
        }
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMControlArea::dumpDynamicData()
{
    RWDBDateTime currentDateTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable dynamicLMControlAreaTable = getDatabase().table( "dynamiclmcontrolarea" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicLMControlAreaTable.updater();

            updater << dynamicLMControlAreaTable["nextchecktime"].assign((RWDBDateTime)getNextCheckTime())
            << dynamicLMControlAreaTable["newpointdatareceivedflag"].assign(RWCString( (getNewPointDataReceivedFlag() ? 'Y':'N') ))
            << dynamicLMControlAreaTable["updatedflag"].assign(RWCString( (getUpdatedFlag() ? 'Y':'N') ))
            << dynamicLMControlAreaTable["controlareastate"].assign( getControlAreaState() )
            << dynamicLMControlAreaTable["currentpriority"].assign( getCurrentPriority() )
            << dynamicLMControlAreaTable["timestamp"].assign((RWDBDateTime)currentDateTime)
            << dynamicLMControlAreaTable["currentdailystarttime"].assign( getCurrentDailyStartTime() )
            << dynamicLMControlAreaTable["currentdailystoptime"].assign( getCurrentDailyStopTime() );

            updater.where(dynamicLMControlAreaTable["deviceid"]==getPAOId());//will be paobjectid

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/

            updater.execute( conn );
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted control area into DynamicLMControlArea: " << getPAOName() << endl;
            }

            RWDBInserter inserter = dynamicLMControlAreaTable.inserter();

            inserter << getPAOId()
            << getNextCheckTime()
            << RWCString( ( getNewPointDataReceivedFlag() ? 'Y': 'N' ) )
            << RWCString( ( getUpdatedFlag() ? 'Y': 'N' ) )
            << getControlAreaState()
            << getCurrentPriority()
            << currentDateTime
            << getCurrentDailyStartTime()
            << getCurrentDailyStopTime();

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }*/

            inserter.execute( conn );

            _insertDynamicDataFlag = FALSE;
        }
    }
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMControlArea::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    RWTime tempTime;
    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _defoperationalstate
    >> _controlinterval
    >> _minresponsetime
    >> _defdailystarttime
    >> _defdailystoptime
    >> _requirealltriggersactiveflag
    >> tempTime
    >> _newpointdatareceivedflag
    >> _updatedflag
    >> _controlareastatuspointid
    >> _controlareastate
    >> _currentpriority
    >> _currentdailystarttime
    >> _currentdailystoptime
    >> _lmcontrolareatriggers
    >> _lmprograms;

    _nextchecktime = RWDBDateTime(tempTime);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMControlArea::saveGuts(RWvostream& ostrm ) const
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _defoperationalstate
    << _controlinterval
    << _minresponsetime
    << _defdailystarttime
    << _defdailystoptime
    << _requirealltriggersactiveflag
    << _nextchecktime.rwtime()
    << _newpointdatareceivedflag
    << _updatedflag
    << _controlareastatuspointid
    << _controlareastate
    << _currentpriority
    << _currentdailystarttime
    << _currentdailystoptime
    << _lmcontrolareatriggers
    << _lmprograms;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::operator=(const CtiLMControlArea& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _defoperationalstate = right._defoperationalstate;
        _controlinterval = right._controlinterval;
        _minresponsetime = right._minresponsetime;
        _defdailystarttime = right._defdailystarttime;
        _defdailystoptime = right._defdailystoptime;
        _requirealltriggersactiveflag = right._requirealltriggersactiveflag;
        _nextchecktime = right._nextchecktime;
        _newpointdatareceivedflag = right._newpointdatareceivedflag;
        _updatedflag = right._updatedflag;
        _controlareastatuspointid = right._controlareastatuspointid;
        _controlareastate = right._controlareastate;
        _currentpriority = right._currentpriority;
        _currentdailystarttime = right._currentdailystarttime;
        _currentdailystoptime = right._currentdailystoptime;

        _lmcontrolareatriggers.clearAndDestroy();
        for(UINT i=0;i<right._lmcontrolareatriggers.entries();i++)
        {
            _lmcontrolareatriggers.insert(((CtiLMControlAreaTrigger*)right._lmcontrolareatriggers[i])->replicate());
        }

        _lmprograms.clearAndDestroy();
        for(UINT j=0;j<right._lmprograms.entries();j++)
        {
            _lmprograms.insert(((CtiLMProgramBase*)right._lmprograms[j])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMControlArea::operator==(const CtiLMControlArea& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMControlArea::operator!=(const CtiLMControlArea& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !operator==(right);
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMControlArea* CtiLMControlArea::replicate() const
{
    return(new CtiLMControlArea(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMControlArea::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBNullIndicator isNull;
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;
    RWCString tempTypeString;
    _insertDynamicDataFlag = FALSE;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["defoperationalstate"] >> _defoperationalstate;
    rdr["controlinterval"] >> _controlinterval;
    rdr["minresponsetime"] >> _minresponsetime;
    rdr["defdailystarttime"] >> _defdailystarttime;
    rdr["defdailystoptime"] >> _defdailystoptime;
    rdr["requirealltriggersactiveflag"] >> tempBoolString;
    tempBoolString.toLower();
    setRequireAllTriggersActiveFlag(tempBoolString=="y"?TRUE:FALSE);

    setControlAreaStatusPointId(0);

    rdr["nextchecktime"] >> isNull;
    if( !isNull )
    {
        rdr["nextchecktime"] >> _nextchecktime;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        tempBoolString.toLower();
        setNewPointDataReceivedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["updatedflag"] >> tempBoolString;
        tempBoolString.toLower();
        setUpdatedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["controlareastate"] >> _controlareastate;
        rdr["currentpriority"] >> _currentpriority;
        rdr["currentdailystarttime"] >> _currentdailystarttime;
        rdr["currentdailystoptime"] >> _currentdailystoptime;
        rdr["timestamp"] >> dynamicTimeStamp;

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        figureNextCheckTime(RWDBDateTime().seconds());
        setNewPointDataReceivedFlag(FALSE);
        setUpdatedFlag(TRUE);//should always be sent to clients if it is newly added!
        setControlAreaState(CtiLMControlArea::InactiveState);
        //current priority set below zero when inactive
        setCurrentPriority(-1);
        setCurrentDailyStartTime(getDefDailyStartTime());
        setCurrentDailyStopTime(getDefDailyStopTime());

        _insertDynamicDataFlag = TRUE;
    }
}

/*---------------------------------------------------------------------------
    getAutomaticallyStartedSignalString


---------------------------------------------------------------------------*/
RWCString* CtiLMControlArea::getAutomaticallyStartedSignalString()
{
    RWCString* returnString = new RWCString("");

    if( _lmcontrolareatriggers.entries() > 0 )
    {
        for(int i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType &&
                ( currentTrigger->getPointValue() > currentTrigger->getThreshold() ||
                  currentTrigger->getProjectedPointValue() > currentTrigger->getThreshold() ) )
            {
                if( returnString->length() > 0 )
                {
                    *returnString += "; ";
                }

                char tempchar[80] = "";
                if( currentTrigger->getPointValue() > currentTrigger->getThreshold() )
                {
                    *returnString += "Pt. Value: ";
                    _snprintf(tempchar,80,"%.*f",1,currentTrigger->getPointValue());
                    *returnString += tempchar;
                }
                else
                {
                    *returnString += "Projected Pt. Value: ";
                    _snprintf(tempchar,80,"%.*f",1,currentTrigger->getProjectedPointValue());
                    *returnString += tempchar;
                }
                *returnString += " > Threshold: ";
                _snprintf(tempchar,80,"%.*f",1,currentTrigger->getThreshold());
                *returnString += tempchar;
            }
            else if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::StatusTriggerType &&
                     currentTrigger->getPointValue() != currentTrigger->getNormalState() )
            {
                if( returnString->length() > 0 )
                {
                    *returnString += "; ";
                }

                char tempchar[80] = "";
                *returnString += " Pt. Status: ";
                _snprintf(tempchar,80,"%.*f",0,currentTrigger->getPointValue());
                *returnString += tempchar;
                *returnString += " != Normal Status: ";
                _ultoa(currentTrigger->getNormalState(),tempchar,10);
                *returnString += tempchar;
            }
        }
    }

    return returnString;
}


/* Public Static members */
const RWCString CtiLMControlArea::DefOpStateEnabled = "Enabled";
const RWCString CtiLMControlArea::DefOpStateDisabled = "Disabled";
const RWCString CtiLMControlArea::DefOpStateNone = "None";

int CtiLMControlArea::InactiveState = STATEZERO;
int CtiLMControlArea::ActiveState = STATEONE;
int CtiLMControlArea::ManualActiveState = STATETWO;
int CtiLMControlArea::ScheduledState = STATETHREE;
int CtiLMControlArea::FullyActiveState = STATEFOUR;

