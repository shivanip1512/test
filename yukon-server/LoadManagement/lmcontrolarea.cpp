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
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "devicetypes.h"
#include "device.h"
#include "loadmanager.h"
#include "resolvers.h"

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
    getCurrentPriority

    Returns the current priority of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentPriority() const
{

    return _currentpriority;
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
    setCurrentPriority

    Sets the current priority of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setCurrentPriority(LONG currpriority)
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

    Returns a BOOLean if any threshold trigger is tripped for the control area.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isThresholdTriggerTripped()
{


    BOOL returnBoolean = FALSE;
    if( _lmcontrolareatriggers.entries() > 0 )
    {
        for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
            if( !currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::ThresholdTriggerType,RWCString::ignoreCase) )
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
    DOUBLE returnLoadReductionNeeded = 0.0;
    if( _lmcontrolareatriggers.entries() > 0 )
    {
        LONG triggersTripped = 0;
        for(LONG i=0;i<_lmcontrolareatriggers.entries();i++)
        {
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

/*---------------------------------------------------------------------------
    reduceControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::reduceControlAreaLoad(DOUBLE loadReductionNeeded, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
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
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState )
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* lmProgramDirect = (CtiLMProgramDirect*)currentLMProgram;
                        if( lmProgramDirect->getDefaultPriority() > getCurrentPriority() )
                        {
                            if( getCurrentPriority() < 0 ||
                                newlyActivePrograms == 0 )
                            {//is inactive or current pass hasn't controlled any new programs
                                {
                                    char tempchar[80];
                                    RWCString text = RWCString("Priority Change Control Area: ");
                                    text += getPAOName();
                                    RWCString additional = RWCString("Previous Priority: ");
                                    _ltoa(getCurrentPriority(),tempchar,10);
                                    additional += tempchar;
                                    additional += " New Priority: ";
                                    _ltoa(lmProgramDirect->getDefaultPriority(),tempchar,10);
                                    additional += tempchar;
                                    CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                    multiDispatchMsg->insert(signal);
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                setCurrentPriority(lmProgramDirect->getDefaultPriority());
                            }
                            else
                            {
                                //load has already been reduced in one priority in this
                                //control area, we only add control to one priority at a
                                //time
                                break;
                            }
                        }

                        expectedLoadReduced = lmProgramDirect->reduceProgramLoad(loadReductionNeeded, getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, isTriggerCheckNeeded(secondsFrom1901));
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

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    takeAllAvailableControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::takeAllAvailableControlAreaLoad(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
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
                            expectedLoadReduced += lmProgramDirect->reduceProgramLoad(0.0, getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, isTriggerCheckNeeded(secondsFrom1901));
                        }
                        if( currentLMProgram->getDefaultPriority() > getCurrentPriority() )
                        {
                            {
                                char tempchar[80];
                                RWCString text = RWCString("Priority Change Control Area: ");
                                text += getPAOName();
                                RWCString additional = RWCString("Previous Priority: ");
                                _ltoa(getCurrentPriority(),tempchar,10);
                                additional += tempchar;
                                additional += " New Priority: ";
                                _ltoa(currentLMProgram->getDefaultPriority(),tempchar,10);
                                additional += tempchar;
                                CtiSignalMsg* signal = new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                multiDispatchMsg->insert(signal);
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << text << ", " << additional << endl;
                                }
                            }
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
            ((CtiLMProgramBase*)_lmprograms[j])->getProgramState() == CtiLMProgramBase::ManualActiveState )
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

/*---------------------------------------------------------------------------
    maintainCurrentControl

    Checks all active programs to make sure that the current gear doesn't
    need to increased.  Refreshes time refresh programs shed times.  Updates
    current hours values in programs as they continue to control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::maintainCurrentControl(LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL examinedControlAreaForControlNeededFlag)
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
            if( ((CtiLMProgramDirect*)currentLMProgram)->maintainProgramControl(getCurrentPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, secondsFrom1901, multiPilMsg, multiDispatchMsg, isPastMinResponseTime(secondsFrom1901), examinedControlAreaForControlNeededFlag) )
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
            _ltoa(getCurrentPriority(),tempchar,10);
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
        setCurrentPriority(newPriority);
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
BOOL CtiLMControlArea::stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901 )
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
            if( ((CtiLMProgramDirect*)currentLMProgram)->stopProgramControl(multiPilMsg, multiDispatchMsg, secondsFrom1901 ) )
            {
                returnBOOL = TRUE;

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
                _ltoa(getCurrentPriority(),tempchar,10);
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
            setCurrentPriority(newPriority);
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
    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getManualControlReceivedFlag() )
        {
            if( currentLMProgram->handleManualControl(secondsFrom1901,multiPilMsg,multiDispatchMsg) )
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
            setCurrentPriority(-1);
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
void CtiLMControlArea::handleTimeBasedControl(ULONG secondsFrom1901, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
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
	    if(currentLMProgram->handleTimedControl(secondsFrom1901, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg))
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
        setControlAreaState(CtiLMControlArea::InactiveState);
        if( previousControlAreaState != CtiLMControlArea::InactiveState )
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
            setCurrentPriority(-1);
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
void CtiLMControlArea::handleNotification(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{
    for(LONG i=0;i<_lmprograms.entries();i++)
    {
        CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)_lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
	{
	    CtiLMProgramDirect* currentLMDirectProgram = (CtiLMProgramDirect*) currentLMProgram;
	    if( currentLMDirectProgram->getNotifyTime().seconds() > RWDBDateTime(1991,1,1,0,0,0,0).seconds() &&
		currentLMDirectProgram->getNotifyTime().seconds() <= secondsFrom1901 )
	    {
		currentLMDirectProgram->notifyGroupsOfStart(multiDispatchMsg);
		currentLMDirectProgram->setNotifyTime(RWDBDateTime(1990,1,1,0,0,0,0));
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

	    if(lm_direct->getDirectStartTime() != start ||
	       lm_direct->getDirectStopTime() != stop)
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
            << dynamicLMControlAreaTable["currentpriority"].assign( getCurrentPriority() )
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
            << getCurrentPriority()
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
        setCurrentPriority(-1);
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

