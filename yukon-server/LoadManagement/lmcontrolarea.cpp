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
#include "yukon.h"

#include "dbaccess.h"
#include "msg_signal.h"

#include "lmcontrolarea.h"
#include "lmcontrolareatrigger.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "lmprogramdirect.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "devicetypes.h"
#include "device.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "numstr.h"

extern ULONG _LM_DEBUG;

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
LONG CtiLMControlArea::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getDefOperationalState

    Returns the default operational state of the control area
---------------------------------------------------------------------------*/
const RWCString& CtiLMControlArea::getDefOperationalState() const
{
    return _defoperationalstate;
}

/*---------------------------------------------------------------------------
    getControlInterval

    Returns the control interval of the control area in seconds
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getControlInterval() const
{
    return _controlinterval;
}

/*---------------------------------------------------------------------------
    getMinResponseTime

    Returns the minimum response time of the control area in seconds
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getMinResponseTime() const
{
    return _minresponsetime;
}

/*---------------------------------------------------------------------------
    getDefDailyStartTime

    Returns the default daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefDailyStartTime() const
{
    return _defdailystarttime;
}

/*---------------------------------------------------------------------------
    getDefDailyStopTime

    Returns the default daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefDailyStopTime() const
{
    return _defdailystoptime;
}

/*---------------------------------------------------------------------------
    getRequireAllTriggersActiveFlag

    Returns the require all triggers active flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getRequireAllTriggersActiveFlag() const
{
    return _requirealltriggersactiveflag;
}

/*---------------------------------------------------------------------------
    getNextCheckTime

    Returns the next check time of the control area
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMControlArea::getNextCheckTime() const
{
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getLMControlAreaTriggers

    Returns the list of triggers for this control area
---------------------------------------------------------------------------*/
RWOrdered& CtiLMControlArea::getLMControlAreaTriggers()
{
    return _lmcontrolareatriggers;
}

/*-----------------------------------------------------------------------------
  getThresholdTrigger

  Return the control area's threshold trigger if there is one.
  Otherwise returns 0.
-----------------------------------------------------------------------------*/  
CtiLMControlAreaTrigger* CtiLMControlArea::getThresholdTrigger() const
{
    for(int i=0;i<_lmcontrolareatriggers.entries();i++)
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if(currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType)
        {
            return currentTrigger;
        }
    }
    return 0;
}

/*---------------------------------------------------------------------------
    getLMPrograms

    Returns the list of programs for this control area
---------------------------------------------------------------------------*/
RWOrdered& CtiLMControlArea::getLMPrograms()
{
    return _lmprograms;
}

/*---------------------------------------------------------------------------
    getNewPointDataReceivedFlag

    Returns the new point data received flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getNewPointDataReceivedFlag() const
{
    return _newpointdatareceivedflag;
}

/*---------------------------------------------------------------------------
    getUpdatedFlag

    Returns the updated flag of the control area
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::getUpdatedFlag() const
{
    return _updatedflag;
}

/*---------------------------------------------------------------------------
    getControlAreaStatusPointId

    Returns the point id of the control area status point
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getControlAreaStatusPointId() const
{
    return _controlareastatuspointid;
}

/*---------------------------------------------------------------------------
    getControlAreaState

    Returns the state of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getControlAreaState() const
{
    return _controlareastate;
}

/*---------------------------------------------------------------------------
    getCurrentStartPriority

    Returns the current priority of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentStartPriority() const
{
    //find the largest positive priority of the active programs
    int start_priority = -1;
    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if(lm_program->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            start_priority = max(lm_program->getStartPriority(), start_priority);
        }
    }
    return start_priority;
}

/*----------------------------------------------------------------------------
  getCurrentStopPriority

  Returns the current stop priority of the control area.
  -1 is there is nothing active.  1 is the high priority possible.
----------------------------------------------------------------------------*/
int CtiLMControlArea::getCurrentStopPriority() 
{
    //find the smallest positive priority of the active programs
    int stop_priority = numeric_limits<int>::max();
    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if(lm_program->getProgramState() != CtiLMProgramBase::InactiveState &&
            lm_program->getStopPriority() > 0)
        {
            stop_priority = min(lm_program->getStopPriority(), stop_priority);
        }
    }
    return (stop_priority == numeric_limits<int>::max() ? -1 : stop_priority);
}

/*---------------------------------------------------------------------------
    getCurrentDailyStartTime

    Returns the current daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentDailyStartTime() const
{

    return _currentdailystarttime;
}

/*---------------------------------------------------------------------------
    getCurrentDailyStopTime

    Returns the default daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentDailyStopTime() const
{

    return _currentdailystoptime;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the pao id of the control area - use with caution
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOId(LONG id)
{

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

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOClass(const RWCString& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOName(const RWCString& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOType(LONG type)
{

    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAODescription(const RWCString& description)
{

    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDisableFlag(BOOL disable)
{

    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setDefOperationalState

    Sets the default operational state of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDefOperationalState(const RWCString& opstate)
{

    _defoperationalstate = opstate;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInterval

    Sets the control interval of the control area in seconds
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlInterval(LONG interval)
{

    _controlinterval = interval;

    return *this;
}

/*---------------------------------------------------------------------------
    setMinResponseTime

    Sets the minimum response time of the control area in seconds
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setMinResponseTime(LONG response)
{

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

    _defdailystoptime = stop;

    return *this;
}

/*---------------------------------------------------------------------------
    setRequireAllTriggersActiveFlag

    Sets the require all triggers active flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setRequireAllTriggersActiveFlag(BOOL requireall)
{

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
    if(_newpointdatareceivedflag != newdatareceived)
    {
        _newpointdatareceivedflag = newdatareceived;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setUpdatedFlag

    Sets the updated flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setUpdatedFlag(BOOL updated)
{
    if(_updatedflag != updated)
    {
        _updatedflag = updated;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setControlAreaStatusPointId

    Sets the point id of the control area status point
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlAreaStatusPointId(LONG statuspointid)
{

    _controlareastatuspointid = statuspointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlAreaState

    Sets the state of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setControlAreaState(LONG state)
{
    if(_controlareastate != state)
    {
        _controlareastate = state;
        setDirty(true);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentStartPriority

    Sets the current priority of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentStartPriority(LONG currpriority)
{
    if(_currentpriority != currpriority)
    {
        _currentpriority = currpriority;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyStartTime

    Sets the current daily start time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentDailyStartTime(LONG tempstart)
{
    if(_currentdailystoptime != tempstart)
    {
        _currentdailystarttime = tempstart;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyStopTime

    Sets the current daily stop time as the number of seconds after
    midnight for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentDailyStopTime(LONG tempstop)
{
    if(_currentdailystoptime != tempstop)
    {
        _currentdailystoptime = tempstop;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    isTriggerCheckNeeded

    Returns a BOOLean if the control area should be checked to determine
    need for control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isTriggerCheckNeeded(ULONG secondsFrom1901)
{
    BOOL returnBoolean = FALSE;

    if(_lmcontrolareatriggers.entries() == 0)
    {
	// No triggers!  no need to check
	return false;
    }
    
    // Are all the triggers initialized with data?  if not no reason to check the triggers
    // Otherwise we might control based on default point values which can be bad
    for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
    {
        CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if(!trigger->hasReceivedPointData())
        {
            return false;
        }
    }
    
    if( getControlInterval() > 0 )
    {
        returnBoolean = getNextCheckTime().seconds() <= secondsFrom1901;
    }
    else
    {
        returnBoolean = getNewPointDataReceivedFlag();
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isControlTime

    Returns a BOOLean if the control area can be controlled at the current
    time and day of the week.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isControlTime(LONG secondsFromBeginningOfDay)
{


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
    BOOL returnBoolean = TRUE;

    DOUBLE currentReduction = 0.0;
    if( _lmprograms.entries() > 0 )
    {
        for(LONG i=0;i<_lmprograms.entries();i++)
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
        LONG triggersStillTripped = 0;
        for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) )
            {
                if( ( (currentTrigger->getPointValue() + currentReduction) >
                      (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) ||
                    ( (currentTrigger->getProjectedPointValue() + currentReduction) >
                      (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) )
                {
                    triggersStillTripped++;
                }
            }
            else if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
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
    BOOL returnBoolean = TRUE;

    if( _lmprograms.entries() > 0 )
    {
        for(LONG i=0;i<_lmprograms.entries();i++)
        {
            CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
            if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
                currentLMProgram->getLastControlSent().seconds() + getMinResponseTime() >= secondsFrom1901 )
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


    BOOL returnBoolean = FALSE;

    if( _lmprograms.entries() > 0 )
    {
        for(LONG i=0;i<_lmprograms.entries();i++)
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

    Returns a BOOLean if any threshold trigger is tripped for the control area
    and optionally program.

    If program is NULL then only the control area will be considered.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isThresholdTriggerTripped(CtiLMProgramBase* program)
{
    BOOL returnBoolean = FALSE;
    
    int offset = 0;
    if(program != 0) {
        offset = ((CtiLMProgramDirect*) program)->getTriggerOffset();
    }
    
    for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) )
        {
            if( currentTrigger->getPointValue() > (currentTrigger->getThreshold()+offset) ||
                currentTrigger->getProjectedPointValue() > (currentTrigger->getThreshold()+offset) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }
    return returnBoolean;
}

BOOL CtiLMControlArea::hasThresholdTrigger()
{
    for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) )
        {
            return TRUE;
        }
    }
    return FALSE;
}

BOOL CtiLMControlArea::hasStatusTrigger() 
   { 
       for(LONG i=0;i<_lmcontrolareatriggers.entries();i++) 
       { 
           CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i]; 
           if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) ) 
           { 
               return TRUE; 
           } 
       } 
       return FALSE; 
 }  

/*---------------------------------------------------------------------------
    isStatusTriggerTripped

    Returns a BOOLean if any status trigger is tripped for the control area
    and optionally program.

    If program is NULL then only the control area will be considered.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isStatusTriggerTripped(CtiLMProgramBase* program)
{
    BOOL returnBoolean = FALSE;
    
    for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
        {
            if( currentTrigger->getPointValue() != currentTrigger->getNormalState())
            {
                returnBoolean = TRUE;
                break;
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
    DOUBLE returnLoadReductionNeeded = 0.0;
    if( _lmcontrolareatriggers.entries() > 0 )
    {
        LONG triggersTripped = 0;
        for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
        {   //why is the load from all triggers added up???
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) )
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
                }

                if( currentTrigger->getThresholdKickPercent() > 0 && currentTrigger->getPeakPointId() <= 0 )
                {
                    DOUBLE oldThreshold = currentTrigger->getThreshold();
                    LONG thresholdKickOffset = currentTrigger->getThresholdKickPercent();
                    LONG amountOverKickValue = currentTrigger->getPointValue() - currentTrigger->getThreshold() - thresholdKickOffset;

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
                            additional += " changed because point value: ";
                            _snprintf(tempchar,80,"%.*f",1,currentTrigger->getPointValue());
                            additional += tempchar;
                            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - " << text << ", " << additional << endl;
                            }
                        }
                    }
                }
            }
            else if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
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

        if( getRequireAllTriggersActiveFlag() && (triggersTripped > 0) && (triggersTripped < _lmcontrolareatriggers.entries()) )
        {
            returnLoadReductionNeeded = 0.0;
            if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - LM Control Area: " << getPAOName() << " has at least one trigger active but cannot automatically start control because not all triggers are active in accordance with the RequireAllTriggersActiveFlag" << endl;
            }
        }
    }

    return returnLoadReductionNeeded;
}

/*----------------------------------------------------------------------------
  calculateStopPriorityLoadReduction

  Figure out how much load would increase if we stopped all the programs
  with the highest stop priority.
----------------------------------------------------------------------------*/
double CtiLMControlArea::calculateExpectedLoadIncrease(int stop_priority)
{
    double total_load_reduction = 0.0;
    
    if(stop_priority < 0)
    {
        return 0.0;
    }

    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT && lm_program->getStopPriority() == stop_priority)
        {
            total_load_reduction += ((CtiLMProgramDirect*)lm_program)->getCurrentLoadReduction();
        }
    }
    return total_load_reduction;
}

/*----------------------------------------------------------------------------
  shouldReduceControl

  Decision as to whether we can stop the programs in the current stop priority
----------------------------------------------------------------------------*/
bool CtiLMControlArea::shouldReduceControl()
{
    int cur_stop_priority = getCurrentStopPriority();
    double cur_load_reduction = calculateExpectedLoadIncrease(cur_stop_priority);

    // false means we should not reduce control control
    // Each trigger has its own answer, start with true and set false if necessary
    bool found_threshold_trig = false;
    bool threshold_trig_chk = true;
    bool found_status_trig = false;
    bool status_trig_chk = true;

    if(_lmcontrolareatriggers.entries() == 0)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "shouldReduceControl() - decision cannot be made since there are no triggers on this control area!" << __FILE__ << "(" << __LINE__ << ")" << endl;
        return false;
    }

    for(int i = 0; i < _lmcontrolareatriggers.entries(); i++)
    {
        CtiLMControlAreaTrigger* lm_trigger = (CtiLMControlAreaTrigger*) _lmcontrolareatriggers[i];
        if(lm_trigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType)
        {
            found_threshold_trig = true;
            if( (lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType &&
                 (lm_trigger->getPointValue() > (lm_trigger->getThreshold() - lm_trigger->getMinRestoreOffset()) ||
                  lm_trigger->getPointValue() + cur_load_reduction > lm_trigger->getThreshold()) ) ||

                (lm_trigger->getProjectionType() != CtiLMControlAreaTrigger::NoneProjectionType &&
                 (lm_trigger->getProjectedPointValue() > (lm_trigger->getThreshold() - lm_trigger->getMinRestoreOffset()) ||
                  lm_trigger->getProjectedPointValue() + cur_load_reduction > lm_trigger->getThreshold()) ) )
            {   // The trigger value is above where we would stop controlling

                threshold_trig_chk = false;
            }
        }
        else if(lm_trigger->getTriggerType() == CtiLMControlAreaTrigger::StatusTriggerType)
        {
            found_status_trig = true;
            if( lm_trigger->getPointValue() != lm_trigger->getNormalState())
            {
                status_trig_chk = false;
            }
        }
    }

    return (getRequireAllTriggersActiveFlag() ?
            // If either trigger indicates we should reduce control then do it 
            ( (found_threshold_trig && threshold_trig_chk) || (found_status_trig && status_trig_chk) ) :
            // All triggers must indicate we should reduce control
            ( (found_threshold_trig && threshold_trig_chk && found_status_trig && status_trig_chk) || 
              (found_threshold_trig && threshold_trig_chk && !found_status_trig) ||
              (found_status_trig && status_trig_chk && !found_threshold_trig)));
}

/*---------------------------------------------------------------------------
    reduceControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::reduceControlAreaLoad(DOUBLE loadReductionNeeded, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    DOUBLE expectedLoadReduced = 0.0;
    LONG newlyActivePrograms = 0;
    LONG fullyActivePrograms = 0;
    LONG activePrograms = 0;

    setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( !currentLMProgram->getDisableFlag() &&
            (!currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) || !currentLMProgram->getControlType().compareTo("Enabled",RWCString::ignoreCase)) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( currentLMProgram->isAvailableToday() &&
                currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() &&
                currentLMProgram->isPastMinRestartTime(secondsFrom1901) )
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    RWCString text = RWCString("Automatic Start, LM Control Area: ");
                    text += getPAOName();
                    RWCString additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);  

                    multiDispatchMsg->insert(signal);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                }

                if( currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::TimedActiveState)
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgram;
                        if( lmProgramDirect->getStartPriority() > getCurrentStartPriority() ) // I think this works because programs are stored in order of default priority(start priority)
                        {
                            if( getCurrentStartPriority() < 0 ||
                                newlyActivePrograms == 0 )
                            {//is inactive or current pass hasn't controlled any new programs
                                {
                                    char tempchar[80];
                                    RWCString text = RWCString("Priority Change Control Area: ");
                                    text += getPAOName();
                                    RWCString additional = RWCString("Previous Priority: ");
                                    _ltoa(getCurrentStartPriority(),tempchar,10);
                                    additional += tempchar;
                                    additional += " New Priority: ";
                                    _ltoa(lmProgramDirect->getStartPriority(),tempchar,10);
                                    additional += tempchar;
                                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                    multiDispatchMsg->insert(signal);
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                setCurrentStartPriority(lmProgramDirect->getStartPriority());

                            }
                            else
                            {
                                //load has already been reduced in one priority in this
                                //control area, we only add control to one priority at a
                                //time
                                break;
                            }
                        }

                        // We need to check the programs trigger threshold offset to make sure we are above it, BUT
                        // if there is no threshold trigger then it doesn't apply.
                        // Likewise if this program has no trigger offset then don't worry about it, let it control
                        if( (!hasThresholdTrigger() ||
                             lmProgramDirect->getTriggerOffset() == 0 ||
                             isThresholdTriggerTripped(lmProgramDirect)) )
//                            (lmProgramDirect->getMaxDailyOps() == 0 ||
//                             lmProgramDirect->getDailyOps() < lmProgramDirect->getMaxDailyOps()))
                        {
                            expectedLoadReduced = lmProgramDirect->reduceProgramLoad(loadReductionNeeded, getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, multiNotifMsg, isTriggerCheckNeeded(secondsFrom1901));

                            if(lmProgramDirect->getProgramState() != CtiLMProgramBase::InactiveState)
                            {   // reduceProgram load might not have been able to do anything,
                                // check before we assume the program went active.
                                newlyActivePrograms++;
                                setUpdatedFlag(TRUE);
                            }
                        }
                        
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
                        if( currentLMProgram->getStartPriority() > getCurrentStartPriority() )
                        {
                            setCurrentPriority(currentLMProgram->getStartPriority());
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
                    if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else
            {
                //possibly attemping control state?
            }
        }
        //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
        else if( currentLMProgram->getControlType().compareTo(CtiLMProgramBase::ManualOnlyType,RWCString::ignoreCase) &&
                 currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) &&
                 currentLMProgram->getControlType().compareTo(CtiLMProgramBase::TimedType,RWCString::ignoreCase) )
        {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unknown LM Program Control Type: " << currentLMProgram->getControlType() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    for(LONG j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::TimedActiveState )
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
    }


    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::ActiveState);
    }

    if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Control cannot go active because no programs are currently available " << endl;
    }
    
    setUpdatedFlag(TRUE);
    return expectedLoadReduced;
}

/*----------------------------------------------------------------------------
  reduceControlAreaControl

  Reduce the amount of control in the control area by one stop priority
----------------------------------------------------------------------------*/  
void CtiLMControlArea::reduceControlAreaControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    int cur_stop_priority = getCurrentStopPriority();
    int num_active_programs = 0;
    
    if(cur_stop_priority < 0)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  " Current stop priority is " << cur_stop_priority << " not reducing control" << __FILE__ << "(" << __LINE__ << ")" << endl;
        return;
    }

    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if(lm_program->getStopPriority() == cur_stop_priority && 
           ( lm_program->getProgramState() == CtiLMProgramBase::ActiveState ||
             lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState ||
             lm_program->getProgramState() == CtiLMProgramBase::NonControllingState ) )
        {
            string text = "Reducing control, LM Control Area: ";
            text += getPAOName();
            string additional = "Stop Priority: ";
            additional += CtiNumStr(cur_stop_priority);
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
            signal->setSOE(1);
            multiDispatchMsg->insert(signal);
            
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime() << " " <<  text << " - " << additional << endl;
        }

        if(lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT)
        {
            CtiLMProgramDirect* lm_program_direct = (CtiLMProgramDirect*) lm_program;
            if(lm_program_direct->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, secondsFrom1901) == FALSE)
            { //the program didn't refused to stop (maybe a constraint was violated like a groups min activate time?)
                //so count this program as still active
                num_active_programs++;
            }
            else
            {
                // Let the world know we just auto stopped?
                lm_program_direct->scheduleStopNotification(RWDBDateTime());            
            }
        }


    }
        else
        {
            if(lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState ||
               lm_program->getProgramState() == CtiLMProgramBase::ManualActiveState ||
               lm_program->getProgramState() == CtiLMProgramBase::TimedActiveState ||
               lm_program->getProgramState() == CtiLMProgramBase::ActiveState )
            {
                num_active_programs++;
            }

        }
    }

    if(num_active_programs == 0)
    { //Looks like we stopped them all
        setControlAreaState(CtiLMControlArea::InactiveState);
        RWCString text = RWCString("Automatic Stop, LM Control Area: ");
        text += getPAOName();
        RWCString additional = RWCString("");//someday we can say why we auto stopped
        CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
        signal->setSOE(1);

        multiDispatchMsg->insert(signal);
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << text << ", " << additional << endl;
    }
    }
    setUpdatedFlag(TRUE);
    return;
}

/*---------------------------------------------------------------------------
    takeAllAvailableControlAreaLoad

    Reduce load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::takeAllAvailableControlAreaLoad(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    DOUBLE expectedLoadReduced = 0.0;
    LONG fullyActivePrograms = 0;
    LONG activePrograms = 0;

    setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( !currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) ||
            !currentLMProgram->getControlType().compareTo("Enabled",RWCString::ignoreCase) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( currentLMProgram->isAvailableToday() &&
                currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() &&
                currentLMProgram->isPastMinRestartTime(secondsFrom1901) &&
                !currentLMProgram->getDisableFlag() )
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    RWCString text = RWCString("Automatic Start, LM Control Area: ");
                    text += getPAOName();
                    RWCString additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
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
                        while( lmProgramDirect->getProgramState() != CtiLMProgramBase::FullyActiveState )
                        {
                            expectedLoadReduced += lmProgramDirect->reduceProgramLoad(0.0, getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, multiNotifMsg, isTriggerCheckNeeded(secondsFrom1901));
                        }
                        if( currentLMProgram->getStartPriority() > getCurrentStartPriority() )
                        {
                            {
                                char tempchar[80];
                                RWCString text = RWCString("Priority Change Control Area: ");
                                text += getPAOName();
                                RWCString additional = RWCString("Previous Priority: ");
                                _ltoa(getCurrentStartPriority(),tempchar,10);
                                additional += tempchar;
                                additional += " New Priority: ";
                                _ltoa(currentLMProgram->getStartPriority(),tempchar,10);
                                additional += tempchar;
                                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                multiDispatchMsg->insert(signal);
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << text << ", " << additional << endl;
                                }
                            }
                            setCurrentStartPriority(currentLMProgram->getStartPriority());
                        }
                    }
                    else if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Load Management can not automatically manage curtailment programs yet. in: " << __FILE__ << " at:" << __LINE__ << endl;
                        /*CtiLMProgramCurtailment* lmProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgram;
                        expectedLoadReduced = lmProgramCurtailment->reduceProgramLoad(loadReductionNeeded, multiPilMsg);
                        if( currentLMProgram->getStartPriority() > getCurrentStartPriority() )
                        {
                            setCurrentPriority(currentLMProgram->getStartPriority());
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

                    if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                        getControlAreaState() != CtiLMControlArea::ActiveState )
                    {
                        setControlAreaState(CtiLMControlArea::ActiveState);
                    }
                }
                else
                {
                    if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            else
            {
                //possibly attemping control state?
            }
        }
        else if( currentLMProgram->getControlType().compareTo(CtiLMProgramBase::ManualOnlyType,RWCString::ignoreCase) &&
                 currentLMProgram->getControlType().compareTo(CtiLMProgramBase::TimedType,RWCString::ignoreCase))
        {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unknown LM Program Control Type in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    for(LONG j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::TimedActiveState)    
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::ActiveState);
    }

    if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Control cannot go active because no programs are currently available " << endl;
    }

    return expectedLoadReduced;
}

void CtiLMControlArea::manuallyStartAllProgramsNow(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    LONG fullyActivePrograms = 0;
    LONG activePrograms = 0;
    
    //setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( !currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) ||
            !currentLMProgram->getControlType().compareTo("Enabled",RWCString::ignoreCase) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( currentLMProgram->isAvailableToday() &&
                currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() &&
                currentLMProgram->isPastMinRestartTime(secondsFrom1901) &&
                !currentLMProgram->getDisableFlag() )
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    RWCString text = RWCString("Manual Status Trigger Start, LM Control Area: ");
                    text += getPAOName();
                    RWCString additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
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
                        CtiLMManualControlRequest* manual_req = new CtiLMManualControlRequest(
                            CtiLMManualControlRequest::START_NOW,
                            lmProgramDirect->getPAOId(),
                            gEndOfRWDBDateTime,
                            RWDBDateTime(),
                            gEndOfRWDBDateTime,
                            1, //gear
                            CtiLMProgramDirect::defaultLMStartPriority,
                            "Server Manual Start",
                            TRUE,
                            FALSE);
                        CtiLMExecutorFactory factory;
                        CtiLMExecutor* executor = factory.createExecutor(manual_req);
                        executor->Execute();
                    }

/*                    if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                        getControlAreaState() != CtiLMControlArea::ActiveState )
                    {
                        setControlAreaState(CtiLMControlArea::ActiveState);
                        }*/               
                }
            }
        }
    }

        for(LONG j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::TimedActiveState)    
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::ActiveState);
    }

    if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Control cannot go active because no programs are currently available " << endl;
    }
}

void CtiLMControlArea::manuallyStopAllProgramsNow(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    LONG fullyActivePrograms = 0;
    LONG activePrograms = 0;
    
    //setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if( !currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed

            RWCString text = RWCString("Manual Status Trigger Stop, LM Control Area: ");
            text += getPAOName();
            RWCString additional = *getAutomaticallyStartedSignalString();
            //additional += getAutomaticallyStartedSignalString();
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }

            if( currentLMProgram->getProgramState() != CtiLMProgramBase::InactiveState )
            {
                if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgram;
                    CtiLMManualControlRequest* manual_req = new CtiLMManualControlRequest(
                        CtiLMManualControlRequest::STOP_NOW,
                        lmProgramDirect->getPAOId(),
                        gEndOfRWDBDateTime,
                        RWDBDateTime(),
                        gEndOfRWDBDateTime,
                        1, //gear
                        CtiLMProgramDirect::defaultLMStartPriority,
                        "Server Manual Stop",
                        TRUE,
                        FALSE);
                    CtiLMExecutorFactory factory;
                    CtiLMExecutor* executor = factory.createExecutor(manual_req);
                    executor->Execute();
                }

/*                    if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                      getControlAreaState() != CtiLMControlArea::ActiveState )
                      {
                      setControlAreaState(CtiLMControlArea::ActiveState);
                      }*/               
            }
        }
    }

        for(LONG j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::TimedActiveState)    
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::ActiveState);
    }
}

/*-----------------------------------------------------------------------------
  stopProgramsBelowThreshold

  Affects Automatic programs only!
  Stops any programs that are below the threshold - the programs restore offset.
  This is intended to be supercede and be independent of stopping based on priority.
  Returns TRUE iff at least one program was stopped.  Fugly!
-----------------------------------------------------------------------------*/  
BOOL CtiLMControlArea::stopProgramsBelowThreshold(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    int fullyActivePrograms = 0;
    int activePrograms = 0;
    bool stopped_program = false;
    
    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if(lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
           lm_program->getControlType() == CtiLMProgramBase::AutomaticType &&
           ( lm_program->getProgramState() == CtiLMProgramBase::ActiveState ||
             lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState) )

        {
            CtiLMProgramDirect* lm_program_direct = (CtiLMProgramDirect*) lm_program;
            CtiLMControlAreaTrigger* lm_trigger = getThresholdTrigger();
            
            if(lm_program_direct->getTriggerRestoreOffset() != 0 && lm_trigger != 0 && 
               ((lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType &&
                 lm_trigger->getPointValue() < (lm_trigger->getThreshold() - lm_program_direct->getTriggerRestoreOffset())) ||
                (lm_trigger->getProjectionType() != CtiLMControlAreaTrigger::NoneProjectionType &&
                 lm_trigger->getProjectedPointValue() < (lm_trigger->getThreshold() - lm_program_direct->getTriggerRestoreOffset()))))
               
            {
                string text = "Stopping program: " + lm_program_direct->getPAOName() + ", trigger threshold is below the programs restore offset";
                string additional = "Trigger Threshold: ";
                additional += CtiNumStr(lm_trigger->getThreshold());
                additional += " Program Restore Offset: ";
                additional += CtiNumStr(lm_program_direct->getTriggerRestoreOffset());

                if(lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType)
                {
                    additional += " Trigger Value: ";
                    additional += CtiNumStr(lm_trigger->getPointValue());
                }
                else
                {
                    additional += " Projection Value: ";
                    additional += CtiNumStr(lm_trigger->getProjectedPointValue());                  
                }
                
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
                signal->setSOE(1);
                multiDispatchMsg->insert(signal);

                if( _LM_DEBUG & LM_DEBUG_STANDARD )                 
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << RWTime() << " " <<  text << " - " << additional << endl;
                }

                if(!(lm_program_direct->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, secondsFrom1901) == FALSE))
                {
                    stopped_program = true;
                }
                else
                {
                    // Let the world know we just auto stopped?
                    lm_program_direct->scheduleStopNotification(RWDBDateTime());                
                }
            }
        }
    }

    for(int j=0;j<_lmprograms.entries();j++)
    {
        if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::TimedActiveState ) 
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.entries() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::ActiveState);
    }
    
    return stopped_program;
}


/*---------------------------------------------------------------------------
    maintainCurrentControl

    Checks all active programs to make sure that the current gear doesn't
    need to increased.  Refreshes time refresh programs shed times.  Updates
    current hours values in programs as they continue to control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::maintainCurrentControl(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL examinedControlAreaForControlNeededFlag)
{
    BOOL returnBoolean = FALSE;
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            (!currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) || !currentLMProgram->getControlType().compareTo("Enabled",RWCString::ignoreCase)) &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( ((CtiLMProgramDirect*)currentLMProgram)->maintainProgramControl(getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, multiNotifMsg, isPastMinResponseTime(secondsFrom1901), examinedControlAreaForControlNeededFlag) )
            {
                returnBoolean = TRUE;
            }
        }
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState  ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::TimedActiveState )
        {
            numberOfFullyActivePrograms++;
            numberOfActivePrograms++;
        }
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
                 currentLMProgram->getProgramState() == CtiLMProgramBase::NonControllingState )
        {
            numberOfActivePrograms++;
        }
    }
    if( numberOfActivePrograms == 0 )
    {
        LONG newPriority = -1;
        setControlAreaState(CtiLMControlArea::InactiveState);
        {
            char tempchar[80];
            RWCString text = RWCString("Priority Change Control Area: ");
            text += getPAOName();
            RWCString additional = RWCString("Previous Priority: ");
            _ltoa(getCurrentStartPriority(),tempchar,10);
            additional += tempchar;
            additional += " New Priority: ";
            _ltoa(newPriority,tempchar,10);
            additional += tempchar;
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

            multiDispatchMsg->insert(signal);
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
        }
        setCurrentStartPriority(newPriority);
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
    return returnBoolean;
}

/*---------------------------------------------------------------------------
    stopAllControl

    Stops all Programs that are controlling in a control area, normally
    because we have left a control window.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901 )
{
    BOOL returnBOOL = FALSE;
    bool sentSignalMsg = false;

    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            ( !currentLMProgram->getControlType().compareTo(CtiLMProgramBase::AutomaticType,RWCString::ignoreCase) ||
              !currentLMProgram->getControlType().compareTo("Enabled",RWCString::ignoreCase) ) &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::NonControllingState ) )
        {// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            if( ((CtiLMProgramDirect*)currentLMProgram)->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, secondsFrom1901 ) )
            {
                returnBOOL = TRUE;

                // Let the world know we just auto stopped?
                ((CtiLMProgramDirect*)currentLMProgram)->scheduleStopNotification(RWDBDateTime());

                if( !sentSignalMsg )
                {
                    RWCString text = RWCString("Automatic Stop, LM Control Area: ");
                    text += getPAOName();
                    RWCString additional = RWCString("");//someday we can say why we auto stopped
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                    sentSignalMsg = true;
                }

                {
                    RWCString text = RWCString("Automatic Stop, LM Program: ");
                    text += currentLMProgram->getPAOName();
                    RWCString additional = RWCString("");//someday we can say why we auto stopped
                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(i+2);

                    multiDispatchMsg->insert(signal);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                }
            }
        }
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState  ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::TimedActiveState )
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
            LONG newPriority = -1;
            setControlAreaState(CtiLMControlArea::InactiveState);
            {
                char tempchar[80];
                RWCString text = RWCString("Priority Change Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("Previous Priority: ");
                _ltoa(getCurrentStartPriority(),tempchar,10);
                additional += tempchar;
                additional += " New Priority: ";
                _ltoa(newPriority,tempchar,10);
                additional += tempchar;
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                multiDispatchMsg->insert(signal);
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
            }
            setCurrentStartPriority(newPriority);
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
void CtiLMControlArea::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getManualControlReceivedFlag() )
        {
            if( currentLMProgram->handleManualControl(secondsFrom1901,multiPilMsg,multiDispatchMsg,multiNotifMsg) )
            {
                setUpdatedFlag(TRUE);
            }
        }
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState  ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::TimedActiveState)
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
            setCurrentStartPriority(-1);
                        setUpdatedFlag(TRUE);
        }
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
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
                    setUpdatedFlag(TRUE);
            }
        }
    }
}

/*---------------------------------------------------------------------------
    handleTimeBasedControl

    Check if we have any time based control programs that need to do
    something and then do it.
---------------------------------------------------------------------------*/
void CtiLMControlArea::handleTimeBasedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{

    // for all timed programs
    // if active and not in a control windows
    // then stop the program
    // if !active and in a control window
    // then start the program
    
    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];

        if(currentLMProgram->getControlType() == CtiLMProgramBase::TimedType )
        {
            if(currentLMProgram->handleTimedControl(secondsFrom1901, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg, multiNotifMsg))
            {
                setUpdatedFlag(TRUE);
            }
        }
        
        if( currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState  ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            currentLMProgram->getProgramState() == CtiLMProgramBase::TimedActiveState )
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
                RWCString text = RWCString("Timed Start, LM Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
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
        if( previousControlAreaState == CtiLMControlArea::ActiveState ||
            previousControlAreaState == CtiLMControlArea::FullyActiveState )
        {
            RWCString text = RWCString("Timed Stop, LMControl Area: ");
            text += getPAOName();
            RWCString additional = RWCString("");
            CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
            
            setControlAreaState(CtiLMControlArea::InactiveState);
            setCurrentStartPriority(-1);
            setUpdatedFlag(TRUE);
        }
    }
    else
    {
        if( getControlAreaState() != CtiLMControlArea::ActiveState &&
            getControlAreaState() != CtiLMControlArea::FullyActiveState )
        {
            setControlAreaState(CtiLMControlArea::ActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                RWCString text = RWCString("Timed Start, LM Control Area: ");
                text += getPAOName();
                RWCString additional = RWCString("");
                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
                    setUpdatedFlag(TRUE);
            }
        }
    }
}

/*----------------------------------------------------------------------------
  handleNotification

  Send out any necessary notifications
----------------------------------------------------------------------------*/
void CtiLMControlArea::handleNotification(ULONG secondsFrom1901, CtiMultiMsg* multiNotifMsg)
{
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
        {
            CtiLMProgramDirect* currentLMDirectProgram = (CtiLMProgramDirect*) currentLMProgram;

            // Notify of activation
            if( currentLMDirectProgram->getNotifyActiveTime() > gInvalidRWDBDateTime &&
                currentLMDirectProgram->getNotifyActiveTime().seconds() <= secondsFrom1901 )
            {
                currentLMDirectProgram->notifyGroupsOfStart(multiNotifMsg);
                currentLMDirectProgram->setNotifyActiveTime(gInvalidRWDBDateTime);
                currentLMDirectProgram->dumpDynamicData();
            }

            //Notify of inactivation
            if( currentLMDirectProgram->getNotifyInactiveTime() > gInvalidRWDBDateTime &&
                currentLMDirectProgram->getNotifyInactiveTime().seconds() <= secondsFrom1901 )
            {
                currentLMDirectProgram->notifyGroupsOfStop(multiNotifMsg);
                currentLMDirectProgram->setNotifyInactiveTime(gInvalidRWDBDateTime);
                currentLMDirectProgram->dumpDynamicData();
            }       
        }
    }    
}

/*---------------------------------------------------------------------------
    createControlStatusPointUpdates

    Create new point data messages that track the state (controlling or not)
    of the control area.
---------------------------------------------------------------------------*/
void CtiLMControlArea::createControlStatusPointUpdates(CtiMultiMsg* multiDispatchMsg)
{
    if( getControlAreaStatusPointId() > 0 )
    {
        if( getControlAreaState() == CtiLMControlArea::ActiveState ||
            getControlAreaState() == CtiLMControlArea::FullyActiveState ||
            getControlAreaState() == CtiLMControlArea::ManualActiveState )
        {//controlling
            multiDispatchMsg->insert(new CtiPointDataMsg(getControlAreaStatusPointId(),STATEONE,NormalQuality,StatusPointType));
        }
        else
        {//not controlling
            multiDispatchMsg->insert(new CtiPointDataMsg(getControlAreaStatusPointId(),STATEZERO,NormalQuality,StatusPointType));
        }
    }

    for(long i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)_lmprograms[i];
        currentLMProgramBase->createControlStatusPointUpdates(multiDispatchMsg);
    }
}

/*----------------------------------------------------------------------------
  updateTimedPrograms

  Updates the start/stop time of all the timed programs in this control
  area
----------------------------------------------------------------------------*/  
void CtiLMControlArea::updateTimedPrograms(LONG secondsFromBeginningOfDay)
{
    for(int i = 0; i < _lmprograms.entries(); i++)
    {
        CtiLMProgramBase* lm_program = (CtiLMProgramBase*) _lmprograms[i];
        if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            lm_program->getControlType() == CtiLMProgramBase::TimedType )
        {
            CtiLMProgramDirect* lm_direct = (CtiLMProgramDirect*) lm_program;

            RWDate today;
            RWDBDateTime start(today);
            RWDBDateTime stop(today);
            
            CtiLMProgramControlWindow* cw = lm_program->getControlWindow(secondsFromBeginningOfDay);
            if(cw == 0)
            {
                cw = lm_program->getNextControlWindow(secondsFromBeginningOfDay);
            }
            
            if(cw != 0)
            { 
                start.addSeconds(cw->getAvailableStartTime());
                stop.addSeconds(cw->getAvailableStopTime());

                //control window could be from earlier in the day, add a day to it to indicate
                //tomorrow
                if( cw->getAvailableStopTime() < secondsFromBeginningOfDay)
                {
                    start.addDays(1);
                    stop.addDays(1);
                }
            }

            if(!lm_direct->getManualControlReceivedFlag() &&
               (lm_direct->getDirectStartTime() != start ||
                lm_direct->getDirectStopTime() != stop))
            {
                lm_direct->setDirectStartTime(start);
                lm_direct->setDirectStopTime(stop);
                setUpdatedFlag(TRUE);
            }
        }
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMControlArea::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMControlArea::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    {
        RWDBTable dynamicLMControlAreaTable = getDatabase().table( "dynamiclmcontrolarea" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicLMControlAreaTable.updater();

            updater << dynamicLMControlAreaTable["nextchecktime"].assign((RWDBDateTime)getNextCheckTime())
            << dynamicLMControlAreaTable["newpointdatareceivedflag"].assign(RWCString( (getNewPointDataReceivedFlag() ? 'Y':'N') ))
            << dynamicLMControlAreaTable["updatedflag"].assign(RWCString( (getUpdatedFlag() ? 'Y':'N') ))
            << dynamicLMControlAreaTable["controlareastate"].assign( getControlAreaState() )
            << dynamicLMControlAreaTable["currentpriority"].assign( getCurrentStartPriority() )
            << dynamicLMControlAreaTable["timestamp"].assign((RWDBDateTime)currentDateTime)
            << dynamicLMControlAreaTable["currentdailystarttime"].assign( getCurrentDailyStartTime() )
            << dynamicLMControlAreaTable["currentdailystoptime"].assign( getCurrentDailyStopTime() );

            updater.where(dynamicLMControlAreaTable["deviceid"]==getPAOId());//will be paobjectid

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }

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
            << getCurrentStartPriority()
            << currentDateTime
            << getCurrentDailyStartTime()
            << getCurrentDailyStopTime();

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }

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
    CtiMemDBObject::operator=(right);
    
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
        for(LONG i=0;i<right._lmcontrolareatriggers.entries();i++)
        {
            _lmcontrolareatriggers.insert(((CtiLMControlAreaTrigger*)right._lmcontrolareatriggers[i])->replicate());
        }

        _lmprograms.clearAndDestroy();
        for(LONG j=0;j<right._lmprograms.entries();j++)
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

    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMControlArea::operator!=(const CtiLMControlArea& right) const
{

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
    setRequireAllTriggersActiveFlag(tempBoolString=="t"?TRUE:FALSE);

    setControlAreaStatusPointId(0);
    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = 0;
        LONG tempPointOffset = 0;
        RWCString tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;
        if( resolvePointType(tempPointType) == StatusPointType &&
            tempPointOffset == 1 )
        {
            setControlAreaStatusPointId(tempPointId);
        }
    }

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
        setDirty(false);
    }
    else
    {
        figureNextCheckTime(RWDBDateTime().seconds());
        setNewPointDataReceivedFlag(FALSE);
        setUpdatedFlag(TRUE);//should always be sent to clients if it is newly added!
        setControlAreaState(CtiLMControlArea::InactiveState);
        //current priority set below zero when inactive
        setCurrentStartPriority(-1);
        setCurrentDailyStartTime(getDefDailyStartTime());
        setCurrentDailyStopTime(getDefDailyStopTime());

        _insertDynamicDataFlag = TRUE;
        setDirty(true);
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
            if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) &&
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
            else if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) &&
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
                _ltoa(currentTrigger->getNormalState(),tempchar,10);
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
int CtiLMControlArea::AttemptingControlState = STATEFIVE;

