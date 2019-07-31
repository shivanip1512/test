#include "precompiled.h"

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
#include "loadmanager.h"
#include "resolvers.h"
#include "numstr.h"
#include "lmconstraint.h"
#include "ctidate.h"
#include "ctitime.h"
#include "utility.h"
#include "lmutility.h"
#include "database_writer.h"
#include "database_util.h"

using std::transform;
using std::string;
using std::endl;
using std::vector;
using std::set;

extern ULONG _LM_DEBUG;
extern set<long> _CHANGED_CONTROL_AREA_LIST;

DEFINE_COLLECTABLE( CtiLMControlArea, CTILMCONTROLAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMControlArea::CtiLMControlArea() :
_paoid(0),
_paoType(0),
_disableflag(false),
_controlinterval(0),
_minresponsetime(0),
_defdailystarttime(0),
_defdailystoptime(0),
_requirealltriggersactiveflag(false),
_newpointdatareceivedflag(false),
_updatedflag(false),
_controlareastatuspointid(0),
_controlareastate(0),
_currentpriority(0),
_currentdailystarttime(0),
_currentdailystoptime(0)
{
}

CtiLMControlArea::CtiLMControlArea(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMControlArea::CtiLMControlArea(const CtiLMControlArea& controlarea)
{
    operator=(controlarea);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMControlArea::~CtiLMControlArea()
{
    delete_container(_lmcontrolareatriggers);
    _lmcontrolareatriggers.clear();

    _lmprograms.clear();
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
const string& CtiLMControlArea::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the control area
---------------------------------------------------------------------------*/
const string& CtiLMControlArea::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the control area
---------------------------------------------------------------------------*/
const string& CtiLMControlArea::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the control area
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getPAOType() const
{
    return _paoType;
}

/*---------------------------------------------------------------------------
    getPAOTypeString

    Returns the pao type string of the control area
---------------------------------------------------------------------------*/
const string& CtiLMControlArea::getPAOTypeString() const
{
    return _paoTypeString;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the control area
---------------------------------------------------------------------------*/
const string& CtiLMControlArea::getPAODescription() const
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
const string& CtiLMControlArea::getDefOperationalState() const
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
    getCurrentStartSecondsFromDayBegin

    Returns the current daily start time seconds from beginning of day
    This should not be used for time handling!
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefStartSecondsFromDayBegin() const
{
    return _defdailystarttime;
}

/*---------------------------------------------------------------------------
    getCurrentStopSecondsFromDayBegin

    Returns the current daily start time seconds from beginning of day
    This should not be used for time handling!
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getDefStopSecondsFromDayBegin() const
{
    return _defdailystoptime;
}

/*---------------------------------------------------------------------------
    getDefDailyStartTime

    Returns the default daily start time
---------------------------------------------------------------------------*/
CtiTime CtiLMControlArea::getDefDailyStartTime(const CtiDate &defaultDate) const
{
    return (_defdailystarttime == -1) ?
        CtiTime::neg_infin :
        GetTimeFromOffsetAndDate(_defdailystarttime, defaultDate);
}

/*---------------------------------------------------------------------------
    getDefDailyStopTime

    Returns the default daily stop time
---------------------------------------------------------------------------*/
CtiTime CtiLMControlArea::getDefDailyStopTime(const CtiDate &defaultDate) const
{
    return (_defdailystoptime == -1) ?
        CtiTime::neg_infin :
        GetTimeFromOffsetAndDate(_defdailystoptime, defaultDate);
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
const CtiTime& CtiLMControlArea::getNextCheckTime() const
{
    return _nextchecktime;
}

/*---------------------------------------------------------------------------
    getLMControlAreaTriggers

    Returns the list of triggers for this control area
---------------------------------------------------------------------------*/
vector<CtiLMControlAreaTrigger*>& CtiLMControlArea::getLMControlAreaTriggers()
{
    return _lmcontrolareatriggers;
}

const vector<CtiLMControlAreaTrigger*>& CtiLMControlArea::getLMControlAreaTriggers() const
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
    for( int i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers.at(i);
        if( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType )
        {
            return currentTrigger;
        }
    }
    return 0;
}

/*-----------------------------------------------------------------------------
  getThresholdPointTrigger

  Return the control area's threshold point trigger if there is one.
  Otherwise returns 0.
-----------------------------------------------------------------------------*/
CtiLMControlAreaTrigger* CtiLMControlArea::getThresholdPointTrigger() const
{
    for each ( CtiLMControlAreaTrigger * currentTrigger in _lmcontrolareatriggers )
    {
        if ( currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdPointTriggerType )
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
vector<CtiLMProgramBaseSPtr>& CtiLMControlArea::getLMPrograms()
{
    return _lmprograms;
}

const vector<CtiLMProgramBaseSPtr>& CtiLMControlArea::getLMPrograms() const
{
    return _lmprograms;
}

LONG CtiLMControlArea::getCurrentPriority() const
{
    return _currentpriority;
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
    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( lm_program->getProgramState() != CtiLMProgramBase::InactiveState )
        {
            start_priority = std::max(lm_program->getStartPriority(), start_priority);
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
    int stop_priority = std::numeric_limits<int>::max();
    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( (lm_program->getProgramState() == CtiLMProgramBase::ActiveState ||
             lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState) &&
            // What other states?
//        if(lm_program->getProgramState() != CtiLMProgramBase::InactiveState &&
            lm_program->getStopPriority() > 0 )
        {
            stop_priority = std::min(lm_program->getStopPriority(), stop_priority);
        }
    }
    return(stop_priority == std::numeric_limits<int>::max() ? -1 : stop_priority);
}

/*---------------------------------------------------------------------------
    getCurrentStartSecondsFromDayBegin

    Returns the current daily start time seconds from beginning of day
    This should not be used for time handling!
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentStartSecondsFromDayBegin() const
{
    return _currentdailystarttime;
}

/*---------------------------------------------------------------------------
    getCurrentStopSecondsFromDayBegin

    Returns the current daily start time seconds from beginning of day
    This should not be used for time handling!
---------------------------------------------------------------------------*/
LONG CtiLMControlArea::getCurrentStopSecondsFromDayBegin() const
{
    return _currentdailystoptime;
}


/*---------------------------------------------------------------------------
    getCurrentDailyStartTime

    Returns the current daily start time.
    NOTE: This adjusts automatically for DST!
---------------------------------------------------------------------------*/
CtiTime CtiLMControlArea::getCurrentDailyStartTime(const CtiDate &defaultDate) const
{
    return (_currentdailystarttime == -1) ?
        CtiTime::neg_infin :
        GetTimeFromOffsetAndDate(_currentdailystarttime, defaultDate);
}

/*---------------------------------------------------------------------------
    getCurrentDailyStopTime

    Returns the default daily stop time
    NOTE: This adjusts automatically for DST!
---------------------------------------------------------------------------*/
CtiTime CtiLMControlArea::getCurrentDailyStopTime(const CtiDate &defaultDate) const
{
    return (_currentdailystoptime == -1) ?
        CtiTime::neg_infin :
        GetTimeFromOffsetAndDate(_currentdailystoptime, defaultDate);
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
CtiLMControlArea& CtiLMControlArea::setPAOCategory(const string& category)
{

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOClass(const string& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAOName(const string& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setPAODescription(const string& description)
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
    if( disable != _disableflag )
    {
        _disableflag = disable;
        setDirty(true);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    setDefOperationalState

    Sets the default operational state of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setDefOperationalState(const string& opstate)
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
CtiLMControlArea& CtiLMControlArea::figureNextCheckTime(CtiTime currentTime)
{

    if( _controlinterval != 0 )
    {
        ULONG tempsum = (currentTime.seconds()-(currentTime.seconds()%_controlinterval))+_controlinterval;
        _nextchecktime = CtiTime(CtiTime(tempsum));
    }
    else
    {
        _nextchecktime = CtiTime();
    }

    return *this;
}

/*---------------------------------------------------------------------------
    setNewPointDataReceivedFlag

    Sets the new point data received flag of the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::setNewPointDataReceivedFlag(BOOL newdatareceived)
{
    if( _newpointdatareceivedflag != newdatareceived )
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
    //For those who follow.. the updated flag is no longer used or cleared. Ever.
    //I only left this function in case someone wanted this functionality back.
    /*if( _updatedflag != updated )
    {
        _updatedflag = updated;
        setDirty(true);
    }*/
    if( updated == TRUE )
    {
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
    if( _controlareastate != state )
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
    if( _currentpriority != currpriority )
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
    if( _currentdailystarttime != tempstart )
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
    if( _currentdailystoptime != tempstop )
    {
        _currentdailystoptime = tempstop;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    resetCurrentDailyStartTime

    Sets the current daily start time to the default for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::resetCurrentDailyStartTime()
{
    if( _currentdailystarttime != _defdailystarttime )
    {
        _currentdailystarttime = _defdailystarttime;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    resetCurrentDailyStopTime

    Sets the current daily stop time to the default for the control area
---------------------------------------------------------------------------*/
CtiLMControlArea& CtiLMControlArea::resetCurrentDailyStopTime()
{
    if( _currentdailystoptime != _defdailystoptime )
    {
        _currentdailystoptime = _defdailystoptime;
        setDirty(true);
    }
    return *this;
}

/*---------------------------------------------------------------------------
    isTriggerCheckNeeded

    Returns a BOOLean if the control area should be checked to determine
    need for control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isTriggerCheckNeeded(CtiTime currentTime)
{
    BOOL returnBoolean = FALSE;

    if( _lmcontrolareatriggers.size() == 0 )
    {
        // No triggers!  no need to check
        return false;
    }

    // Are all the triggers initialized with data?  if not no reason to check the triggers
    // Otherwise we might control based on default point values which can be bad
    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* trigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers.at(i);
        if( !trigger->hasReceivedPointData() )
        {
            return false;
        }
    }

    if( getControlInterval() > 0 )
    {
        returnBoolean = getNextCheckTime() <= currentTime;
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

    CtiTime nowTime(0, 0, 0);
    nowTime += secondsFromBeginningOfDay;

    CtiTime tempCurrentDailyStartTime = getCurrentDailyStartTime();
    CtiTime tempCurrentDailyStopTime  = getCurrentDailyStopTime();

    if( getCurrentDailyStartTime().is_special() )
    {
        tempCurrentDailyStartTime = nowTime; //no restrictions here!
    }
    if( getCurrentDailyStopTime().is_special() )
    {
        tempCurrentDailyStopTime = gEndOfCtiTime; // crazy far out in the future!
    }


    if( tempCurrentDailyStartTime <= nowTime && nowTime <= tempCurrentDailyStopTime )
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

    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getReductionTotal() > 0.0 )
        {
            currentReduction += currentLMProgram->getReductionTotal();
        }
    }

    LONG triggersStillTripped = 0;
    for(int i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdTriggerType) ||
            ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdPointTriggerType) )
        {
            if( ( (currentTrigger->getPointValue() + currentReduction) >
                  (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) ||
                ( (currentTrigger->getProjectedPointValue() + currentReduction) >
                  (currentTrigger->getThreshold() - currentTrigger->getMinRestoreOffset()) ) )
            {
                triggersStillTripped++;
            }
        }
        else if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::StatusTriggerType) )
        {
            if( currentTrigger->getPointValue() != currentTrigger->getNormalState() )
            {
                triggersStillTripped++;
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unknown Trigger Type");
        }


        if( triggersStillTripped == 0 )
        {
            returnBoolean = FALSE;
        }
        else if( getRequireAllTriggersActiveFlag() && ( triggersStillTripped < _lmcontrolareatriggers.size() ) )
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
BOOL CtiLMControlArea::isPastMinResponseTime(CtiTime currentTime)
{
    BOOL returnBoolean = TRUE;

    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            currentLMProgram->getLastControlSent() + getMinResponseTime() >= currentTime )
        {
            returnBoolean = FALSE;
            break;
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


    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getManualControlReceivedFlag() )
        {
            returnBoolean = TRUE;
            break;
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    clearManualControlReceivedFlags

    clears manual control received flags for all of the programs in the control area
    have received a manual control.
---------------------------------------------------------------------------*/
void CtiLMControlArea::clearManualControlReceivedFlags()
{
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        currentLMProgram->setManualControlReceivedFlag(FALSE);
    }
    return;
}


/*---------------------------------------------------------------------------
    isThresholdTriggerTripped

    Returns a BOOLean if any threshold trigger is tripped for the control area
    and optionally program.

    If program is NULL then only the control area will be considered.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isThresholdTriggerTripped(CtiLMProgramBaseSPtr program)
{
    BOOL returnBoolean = FALSE;

    int offset = 0;
    if( program )
    {
        offset = boost::static_pointer_cast< CtiLMProgramDirect > (program)->getTriggerOffset();
    }

    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdTriggerType) ||
            ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdPointTriggerType) )
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
    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdTriggerType) ||
            ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdPointTriggerType) )
        {
            return TRUE;
        }
    }
    return FALSE;
}

BOOL CtiLMControlArea::hasStatusTrigger()
{
    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(),CtiLMControlAreaTrigger::StatusTriggerType) )
        {
            return TRUE;
        }
    }
    return FALSE;
}

/*--------------------------------------------------------------------------
   isStatusTriggerTripped

    Returns a BOOLean if any status trigger is tripped for the control area
    and optionally program.

    If program is NULL then only the control area will be considered.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::isStatusTriggerTripped(CtiLMProgramBaseSPtr program)
{
    BOOL returnBoolean = FALSE;

    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(),CtiLMControlAreaTrigger::StatusTriggerType) )
        {
            if( currentTrigger->getPointValue() != currentTrigger->getNormalState() )
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
    LONG triggersTripped = 0;
    for( LONG i=0;i<_lmcontrolareatriggers.size();i++ )   //why is the load from all triggers added up???
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdTriggerType) ||
            ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdPointTriggerType) )
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
                        string text("Automatic Threshold Kick Up");
                        string additional("Threshold for Trigger: ");
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
                        CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                        CTILOG_INFO(dout, text << ", " << additional);
                    }
                }
            }
        }
        else if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::StatusTriggerType) )
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
            CTILOG_INFO(dout, "Unknown Trigger Type");
        }


        if( getRequireAllTriggersActiveFlag() && (triggersTripped > 0) && (triggersTripped < _lmcontrolareatriggers.size()) )
        {
            returnLoadReductionNeeded = 0.0;
            if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
            {
                CTILOG_DEBUG(dout, "LM Control Area: " << getPAOName() << " has at least one trigger active but cannot automatically start control because not all triggers are active in accordance with the RequireAllTriggersActiveFlag");
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

    if( stop_priority < 0 )
    {
        return 0.0;
    }

    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT && lm_program->getStopPriority() == stop_priority )
        {
            total_load_reduction += boost::static_pointer_cast< CtiLMProgramDirect >(lm_program)->getCurrentLoadReduction();
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

    if( _lmcontrolareatriggers.size() == 0 )
    {
        CTILOG_ERROR(dout, "decision cannot be made since there are no triggers on this control area!");
        return false;
    }

    long notActiveTriggerCount = 0;

    for( int i = 0; i < _lmcontrolareatriggers.size(); i++ )
    {
        CtiLMControlAreaTrigger* lm_trigger = (CtiLMControlAreaTrigger*) _lmcontrolareatriggers[i];

        if( lm_trigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdTriggerType ||
            lm_trigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdPointTriggerType )
        {
            double triggerValue = lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType
                                    ? lm_trigger->getPointValue()
                                    : lm_trigger->getProjectedPointValue();

            double activeThresholdOffset = std::max( lm_trigger->getMinRestoreOffset(), cur_load_reduction );

            if ( triggerValue <= ( lm_trigger->getThreshold() - activeThresholdOffset ) )
            {
                notActiveTriggerCount++;
            }
        }
        else if( lm_trigger->getTriggerType() == CtiLMControlAreaTrigger::StatusTriggerType &&
                 lm_trigger->getPointValue() == lm_trigger->getNormalState() )
        {
            notActiveTriggerCount++;
        }
    }

    // Return TRUE if either ALL triggers are inactive, or our flags indicate we want to stop if ANY triggers are inactive.
    return (notActiveTriggerCount == _lmcontrolareatriggers.size()) || (getRequireAllTriggersActiveFlag() && notActiveTriggerCount > 0);
}

/*---------------------------------------------------------------------------
    reduceControlAreaLoad

    Reduces load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::reduceControlAreaLoad(DOUBLE loadReductionNeeded, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    DOUBLE expectedLoadReduced = 0.0;
    LONG newlyActivePrograms = 0;

    setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        CtiLMProgramConstraintChecker con_checker(*((CtiLMProgramDirect*)currentLMProgram.get()), currentTime);

        if( !currentLMProgram->getDisableFlag() &&
            (ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::AutomaticType) || ciStringEqual(currentLMProgram->getControlType(), "Enabled")) )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
        {
            if( con_checker.checkAutomaticProgramConstraints(currentTime, gEndOfCtiTime) )
            /*            if( currentLMProgram->isAvailableToday() &&
                            currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                            currentLMProgram->hasControlHoursAvailable() &&
                            currentLMProgram->isPastMinRestartTime(currentTime) ) */
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    string text("Automatic Start, LM Control Area: ");
                    text += getPAOName();
                    string additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
                }

                if( currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::TimedActiveState )
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirectSPtr lmProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);
                        if( lmProgramDirect->getStartPriority() > getCurrentStartPriority() ) // I think this works because programs are stored in order of default priority(start priority)
                        {
                            if( getCurrentStartPriority() < 0 ||
                                newlyActivePrograms == 0 )//is inactive or current pass hasn't controlled any new programs
                            {
                                {
                                    char tempchar[80];
                                    string text("Priority Change Control Area: ");
                                    text += getPAOName();
                                    string additional("Previous Priority: ");
                                    _ltoa(getCurrentStartPriority(),tempchar,10);
                                    additional += tempchar;
                                    additional += " New Priority: ";
                                    _ltoa(lmProgramDirect->getStartPriority(),tempchar,10);
                                    additional += tempchar;
                                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                    multiDispatchMsg->insert(signal);
                                    CTILOG_INFO(dout, text << ", " << additional);
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
                            lmProgramDirect->setLastUser("(yukon system)");
                            lmProgramDirect->setChangeReason("Automatic Threshold Trigger");
                            expectedLoadReduced = lmProgramDirect->reduceProgramLoad(loadReductionNeeded, getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, currentTime, multiPilMsg, multiDispatchMsg, multiNotifMsg, isTriggerCheckNeeded(currentTime));

                            if( lmProgramDirect->getProgramState() != CtiLMProgramBase::InactiveState )   // reduceProgram load might not have been able to do anything,
                            {
                                // check before we assume the program went active.
                                newlyActivePrograms++;
                                setUpdatedFlag(TRUE);
                            }
                        }

                        if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                            getControlAreaState() != CtiLMControlArea::PartiallyActiveState )
                        {
                            setControlAreaState(CtiLMControlArea::PartiallyActiveState);
                        }
                    }
                    else if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CTILOG_INFO(dout, "Load Management can not automatically manage curtailment programs yet.");
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
                        CTILOG_INFO(dout, "Unknown LM Program Type");
                    }
                }
                else
                {
                    if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
                    {
                        CTILOG_DEBUG(dout, "LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState());
                    }
                }
            }
            else
            {
                //possibly attemping control state?
            }
        }
        else if ( !(ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::ManualOnlyType) ||
                    ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::AutomaticType) ||
                    ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::TimedType)) )
        CTILOG_INFO(dout, "Unknown LM Program Control Type: " << currentLMProgram->getControlType());
    }

    updateStateFromPrograms();

    if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
    {
        CTILOG_INFO(dout, "Control cannot go active because no programs are currently available ");
    }

    //setUpdatedFlag(TRUE);
    return expectedLoadReduced;
}

/*----------------------------------------------------------------------------
  reduceControlAreaControl

  Reduce the amount of control in the control area by one stop priority
----------------------------------------------------------------------------*/
void CtiLMControlArea::reduceControlAreaControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    int cur_stop_priority = getCurrentStopPriority();
    int num_active_programs = 0;

    if( cur_stop_priority < 0 )
    {
        CTILOG_INFO(dout, "Control Area: " << getPAOName() <<  ", Current stop priority is " << cur_stop_priority << " not reducing control");
        return;
    }

    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( lm_program->getStopPriority() == cur_stop_priority &&
            ( lm_program->getProgramState() == CtiLMProgramBase::ActiveState ||
              lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState ||
              lm_program->getProgramState() == CtiLMProgramBase::NonControllingState ) )
        {
            string text = "Reducing control, LM Control Area: ";
            text += getPAOName();
            string additional = "Stop Priority: ";
            additional += CtiNumStr(cur_stop_priority);
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.c_str(),additional.c_str(),GeneralLogType,SignalEvent);
            signal->setSOE(1);
            multiDispatchMsg->insert(signal);

            if( _LM_DEBUG && LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, text << " - " << additional);
            }

            if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMProgramDirectSPtr lm_program_direct = boost::static_pointer_cast< CtiLMProgramDirect >(lm_program);
                lm_program_direct->setChangeReason("Reducing Control");
                if( lm_program_direct->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime) )
                {
                    // Let the world know we just auto stopped?
                    lm_program_direct->scheduleStopNotification(CtiTime());
                }
            }
        }

        if( lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            lm_program->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            lm_program->getProgramState() == CtiLMProgramBase::TimedActiveState ||
            lm_program->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            num_active_programs++;
        }
    }

    if( num_active_programs == 0 ) //Looks like we stopped them all
    {
        setControlAreaState(CtiLMControlArea::InactiveState);
        string text("Automatic Stop, LM Control Area: ");
        text += getPAOName();
        string additional("");//someday we can say why we auto stopped
        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
        signal->setSOE(1);

        multiDispatchMsg->insert(signal);
        CTILOG_INFO(dout, text << ", " << additional);
    }
    setUpdatedFlag(TRUE);
    return;

}

/*---------------------------------------------------------------------------
    takeAllAvailableControlAreaLoad

    Reduce load in the control area by running through the lmprograms to
    determine current priority and controlling one or more lmprograms
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlArea::takeAllAvailableControlAreaLoad(LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    DOUBLE expectedLoadReduced = 0.0;

    setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to

    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];

        if( ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::AutomaticType) ||
            ciStringEqual(currentLMProgram->getControlType(), "Enabled") )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
        {
            if( currentLMProgram->isAvailableToday() &&
                currentLMProgram->isWithinValidControlWindow(secondsFromBeginningOfDay) &&
                currentLMProgram->hasControlHoursAvailable() &&
                currentLMProgram->isPastMinRestartTime(currentTime) &&
                !currentLMProgram->getDisableFlag() )
            {
                if( getControlAreaState() == CtiLMControlArea::InactiveState )
                {
                    string text("Automatic Start, LM Control Area: ");
                    text += getPAOName();
                    string additional = *getAutomaticallyStartedSignalString();
                    //additional += getAutomaticallyStartedSignalString();
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
                }

                if( currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                    currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState )
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirectSPtr lmProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);
                        while( lmProgramDirect->getProgramState() != CtiLMProgramBase::FullyActiveState )
                        {
                            expectedLoadReduced += lmProgramDirect->reduceProgramLoad(0.0, getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, currentTime, multiPilMsg, multiDispatchMsg, multiNotifMsg, isTriggerCheckNeeded(currentTime));
                        }
                        if( currentLMProgram->getStartPriority() > getCurrentStartPriority() )
                        {
                            {
                                char tempchar[80];
                                string text("Priority Change Control Area: ");
                                text += getPAOName();
                                string additional("Previous Priority: ");
                                _ltoa(getCurrentStartPriority(),tempchar,10);
                                additional += tempchar;
                                additional += " New Priority: ";
                                _ltoa(currentLMProgram->getStartPriority(),tempchar,10);
                                additional += tempchar;
                                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                                multiDispatchMsg->insert(signal);
                                CTILOG_INFO(dout, text << ", " << additional);
                            }
                            setCurrentStartPriority(currentLMProgram->getStartPriority());
                        }
                    }
                    else if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CTILOG_INFO(dout, "Load Management can not automatically manage curtailment programs yet.");
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
                        CTILOG_INFO(dout, "Unknown LM Program Type");
                    }

                    if( getControlAreaState() != CtiLMControlArea::FullyActiveState &&
                        getControlAreaState() != CtiLMControlArea::PartiallyActiveState )
                    {
                        setControlAreaState(CtiLMControlArea::PartiallyActiveState);
                    }
                }
                else
                {
                    if( _LM_DEBUG & LM_DEBUG_CONTROL_PARAMS )
                    {
                        CTILOG_DEBUG(dout, "LM Program: " << currentLMProgram->getPAOName() << " already fully active, can not control any further, state: " << currentLMProgram->getProgramState());
                    }
                }
            }
            else
            {
                //possibly attemping control state?
            }
        }
        else if( !(ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::ManualOnlyType)) &&
                 !(ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::TimedType)))
        CTILOG_INFO(dout, "Unknown LM Program Control Type");
    }

    updateStateFromPrograms();

    if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
    {
        CTILOG_INFO(dout, "Control cannot go active because no programs are currently available ");
    }

    return expectedLoadReduced;
}

void CtiLMControlArea::manuallyStartAllProgramsNow(LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    //setControlAreaState(CtiLMControlArea::AttemptingControlState);//if none the the programs are available then we can't control, but we want to
    if( isControlTime(secondsFromBeginningOfDay) )
    {
        for( LONG i=0;i<_lmprograms.size();i++ )
        {
            CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];


            if( ciStringEqual(currentLMProgram->getControlType(),CtiLMProgramBase::AutomaticType) ||
                ciStringEqual(currentLMProgram->getControlType(),"Enabled") )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
            {
                CtiLMProgramConstraintChecker con_checker(*((CtiLMProgramDirect*)currentLMProgram.get()), currentTime);
                //Check manual but not notification time
                if( con_checker.checkSeason(currentTime, gEndOfCtiTime) &&
                    con_checker.checkWeekDays(currentTime, gEndOfCtiTime) &&
                    con_checker.checkMasterActive() &&
                    con_checker.checkControlWindows(currentTime, gEndOfCtiTime) &&
                    con_checker.checkControlAreaControlWindows(*this, currentTime, gEndOfCtiTime, CtiDate::now()) )
                {
                    if( getControlAreaState() == CtiLMControlArea::InactiveState )
                    {
                        string text("Manual Status Trigger Start, LM Control Area: ");
                        text += getPAOName();
                        string additional = *getAutomaticallyStartedSignalString();
                        //additional += getAutomaticallyStartedSignalString();
                        CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                        signal->setSOE(1);

                        multiDispatchMsg->insert(signal);
                        CTILOG_INFO(dout, text << ", " << additional);
                    }

                    if( currentLMProgram->getProgramState() == CtiLMProgramBase::InactiveState &&
                        hasStatusTrigger() && isStatusTriggerTripped() &&
                        currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT)
                    {
                        boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->setControlActivatedByStatusTrigger(TRUE);
                    }
                    if( (currentLMProgram->getProgramState() != CtiLMProgramBase::FullyActiveState &&
                         currentLMProgram->getProgramState() != CtiLMProgramBase::ManualActiveState &&
                         currentLMProgram->getProgramState() != CtiLMProgramBase::ScheduledState) ||

                        // If the program is manual active and ramping out we need to be able to turn
                        // the program around and start ramping in or at least cancel the ramp out
                        (currentLMProgram->getProgramState() == CtiLMProgramBase::ManualActiveState &&
                         boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->getIsRampingOut()) )
                    {
                        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            CtiLMProgramDirectSPtr lmProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);
                            CtiLMManualControlRequest* manual_req = CTIDBG_new CtiLMManualControlRequest(
                                                                                                        CtiLMManualControlRequest::START_NOW,
                                                                                                        lmProgramDirect->getPAOId(),
                                                                                                        gEndOfCtiTime,
                                                                                                        CtiTime(),
                                                                                                        gEndOfCtiTime,
                                                                                                        1, //gear
                                                                                                        CtiLMProgramDirect::defaultLMStartPriority,
                                                                                                        "Server Manual Start",
                                                                                                        CtiLMManualControlRequest::USE_CONSTRAINTS,
                                                                                                        "Automatic");
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

        updateStateFromPrograms();

        if( getControlAreaState() == CtiLMControlArea::AttemptingControlState )
        {
            CTILOG_INFO(dout, "Control cannot go active because no programs are currently available ");
        }
    }
}

void CtiLMControlArea::manuallyStopAllProgramsNow(LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, bool forceAll)
{
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];

        if( ciStringEqual(currentLMProgram->getControlType(),CtiLMProgramBase::AutomaticType) )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
        {

            string text("Manual Status Trigger Stop, LM Control Area: ");
            text += getPAOName();
            string additional = *getAutomaticallyStartedSignalString();
            //additional += getAutomaticallyStartedSignalString();
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);

            CTILOG_INFO(dout, text << ", " << additional);

            if( forceAll &&
                (currentLMProgram->getProgramState() != CtiLMProgramBase::InactiveState ||
                (currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState &&
                 boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->getIsRampingIn())) )
            {
                if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    CtiLMProgramDirectSPtr lmProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);
                    CtiLMManualControlRequest* manual_req = CTIDBG_new CtiLMManualControlRequest(
                                                                                                CtiLMManualControlRequest::STOP_NOW,
                                                                                                lmProgramDirect->getPAOId(),
                                                                                                gEndOfCtiTime,
                                                                                                CtiTime(),
                                                                                                gEndOfCtiTime,
                                                                                                1, //gear
                                                                                                CtiLMProgramDirect::defaultLMStartPriority,
                                                                                                "Server Manual Stop",
                                                                                                CtiLMManualControlRequest::USE_CONSTRAINTS,
                                                                                                "Manual");
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
            else
            {
                if( currentLMProgram->getProgramState() == CtiLMProgramBase::ScheduledState &&
                     boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->wasControlActivatedByStatusTrigger())
                {
                    if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirectSPtr lmProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);
                        CtiLMManualControlRequest* manual_req = CTIDBG_new CtiLMManualControlRequest(
                                                                                                    CtiLMManualControlRequest::STOP_NOW,
                                                                                                    lmProgramDirect->getPAOId(),
                                                                                                    gEndOfCtiTime,
                                                                                                    CtiTime(),
                                                                                                    gEndOfCtiTime,
                                                                                                    1, //gear
                                                                                                    CtiLMProgramDirect::defaultLMStartPriority,
                                                                                                    "Server Manual Stop",
                                                                                                    CtiLMManualControlRequest::USE_CONSTRAINTS,
                                                                                                    "Automatic");
                        CtiLMExecutorFactory factory;
                        CtiLMExecutor* executor = factory.createExecutor(manual_req);
                        executor->Execute();
                    }
                }

            }
        }
    }

    updateStateFromPrograms();
}

/*-----------------------------------------------------------------------------
  stopProgramsBelowThreshold

  Affects Automatic programs only!
  Stops any programs that are below the threshold - the programs restore offset.
  This is intended to be supercede and be independent of stopping based on priority.
  Returns TRUE iff at least one program was stopped.  Fugly!
-----------------------------------------------------------------------------*/
BOOL CtiLMControlArea::stopProgramsBelowThreshold(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    bool stopped_program = false;

    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            lm_program->getControlType() == CtiLMProgramBase::AutomaticType &&
            ( lm_program->getProgramState() == CtiLMProgramBase::ActiveState ||
              lm_program->getProgramState() == CtiLMProgramBase::FullyActiveState) )
        {
            CtiLMProgramDirectSPtr lm_program_direct = boost::static_pointer_cast< CtiLMProgramDirect >(lm_program);

            CtiLMControlAreaTrigger* lm_trigger = getThresholdTrigger();    // try with a regular threshold trigger
            if ( lm_trigger == 0 )
            {
                lm_trigger = getThresholdPointTrigger();    // try again with a threshold point trigger
            }

            if( lm_program_direct->getTriggerRestoreOffset() != 0 && lm_trigger != 0 &&
                ((lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType &&
                  lm_trigger->getPointValue() < (lm_trigger->getThreshold() - lm_program_direct->getTriggerRestoreOffset())) ||
                 (lm_trigger->getProjectionType() != CtiLMControlAreaTrigger::NoneProjectionType &&
                  lm_trigger->getProjectedPointValue() < (lm_trigger->getThreshold() - lm_program_direct->getTriggerRestoreOffset()))) )
            {
                string text = "Stopping program: " + lm_program_direct->getPAOName() + ", trigger threshold is below the programs restore offset";
                string additional = "Trigger Threshold: ";
                additional += CtiNumStr(lm_trigger->getThreshold());
                additional += " Program Restore Offset: ";
                additional += CtiNumStr(lm_program_direct->getTriggerRestoreOffset());

                if( lm_trigger->getProjectionType() == CtiLMControlAreaTrigger::NoneProjectionType )
                {
                    additional += " Trigger Value: ";
                    additional += CtiNumStr(lm_trigger->getPointValue());
                }
                else
                {
                    additional += " Projection Value: ";
                    additional += CtiNumStr(lm_trigger->getProjectedPointValue());
                }

                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text.data(),additional.data(),GeneralLogType,SignalEvent);
                signal->setSOE(1);
                multiDispatchMsg->insert(signal);

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, text << " - " << additional);
                }

                lm_program_direct->setChangeReason("Threshold Stop");
                if( lm_program_direct->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime) )
                {
                    // Let the world know we just auto stopped?
                    lm_program_direct->scheduleStopNotification(CtiTime());

                    stopped_program = true;
                }
            }
        }
    }

    updateStateFromPrograms();

    return stopped_program;
}


/*---------------------------------------------------------------------------
    maintainCurrentControl

    Checks all active programs to make sure that the current gear doesn't
    need to increased.  Refreshes time refresh programs shed times.  Updates
    current hours values in programs as they continue to control.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::maintainCurrentControl(LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL examinedControlAreaForControlNeededFlag)
{
    BOOL returnBoolean = FALSE;
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    LONG numberOfScheduledPrograms = 0;

    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            (ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::AutomaticType) || ciStringEqual(currentLMProgram->getControlType(), "Enabled")) &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ) )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
        {
            if( boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->maintainProgramControl(getCurrentStartPriority(), _lmcontrolareatriggers, secondsFromBeginningOfDay, currentTime, multiPilMsg, multiDispatchMsg, multiNotifMsg, isPastMinResponseTime(currentTime), examinedControlAreaForControlNeededFlag) )
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
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ScheduledState )
        {
            numberOfScheduledPrograms++;
        }
    }
    if( numberOfActivePrograms == 0 && numberOfScheduledPrograms <= 0 )
    {
        LONG newPriority = -1;
        setControlAreaState(CtiLMControlArea::InactiveState);
        {
            char tempchar[80];
            string text("Priority Change Control Area: ");
            text += getPAOName();
            string additional("Previous Priority: ");
            _ltoa(getCurrentStartPriority(),tempchar,10);
            additional += tempchar;
            additional += " New Priority: ";
            _ltoa(newPriority,tempchar,10);
            additional += tempchar;
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

            multiDispatchMsg->insert(signal);
            CTILOG_INFO(dout, text << ", " << additional);
        }
        setCurrentStartPriority(newPriority);
    }
    else if( numberOfFullyActivePrograms > 0 &&
             numberOfFullyActivePrograms == _lmprograms.size() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( numberOfScheduledPrograms > 0 && numberOfActivePrograms <= 0 )
    {
        if( numberOfScheduledPrograms < _lmprograms.size() )
        {
            setControlAreaState(CtiLMControlArea::PartiallyScheduledState);
        }
        else
        {
            setControlAreaState(CtiLMControlArea::FullyScheduledState);
        }
    }
    else
    {
        setControlAreaState(CtiLMControlArea::PartiallyActiveState);
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    stopAllControl

    Stops all Programs that are controlling in a control area, normally
    because we have left a control window.
---------------------------------------------------------------------------*/
BOOL CtiLMControlArea::stopAllControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime )
{
    BOOL returnBOOL = FALSE;
    bool sentSignalMsg = false;

    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    LONG numberOfScheduledPrograms = 0;
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            ( ciStringEqual(currentLMProgram->getControlType(), CtiLMProgramBase::AutomaticType) ||
              ciStringEqual(currentLMProgram->getControlType(), "Enabled") ) &&
            ( currentLMProgram->getProgramState() == CtiLMProgramBase::ActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::FullyActiveState ||
              currentLMProgram->getProgramState() == CtiLMProgramBase::NonControllingState ) )// HACK: == "Enabled" part above should be removed as soon as the editor is fixed
        {
            boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->setChangeReason("Control Area Stop");
            if( boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->stopProgramControl(multiPilMsg, multiDispatchMsg, multiNotifMsg, currentTime ) )
            {
                returnBOOL = TRUE;

                // Let the world know we just auto stopped?
                boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->scheduleStopNotification(CtiTime());

                if( !sentSignalMsg )
                {
                    string text("Automatic Stop, LM Control Area: ");
                    text += getPAOName();
                    string additional("");//someday we can say why we auto stopped
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(1);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
                    sentSignalMsg = true;
                }

                {
                    string text("Automatic Stop, LM Program: ");
                    text += currentLMProgram->getPAOName();
                    string additional("");//someday we can say why we auto stopped
                    CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                    signal->setSOE(i+2);

                    multiDispatchMsg->insert(signal);
                    CTILOG_INFO(dout, text << ", " << additional);
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
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ScheduledState )
        {
            numberOfScheduledPrograms++;
        }
    }
    if( returnBOOL )
    {
        if( numberOfActivePrograms == 0 && numberOfScheduledPrograms <= 0 )
        {
            LONG newPriority = -1;
            setControlAreaState(CtiLMControlArea::InactiveState);
            {
                char tempchar[80];
                string text("Priority Change Control Area: ");
                text += getPAOName();
                string additional("Previous Priority: ");
                _ltoa(getCurrentStartPriority(),tempchar,10);
                additional += tempchar;
                additional += " New Priority: ";
                _ltoa(newPriority,tempchar,10);
                additional += tempchar;
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            }
            setCurrentStartPriority(newPriority);
        }
        else if( numberOfFullyActivePrograms > 0 &&
                 numberOfFullyActivePrograms == _lmprograms.size() )
        {
            setControlAreaState(CtiLMControlArea::FullyActiveState);
        }
        else if( numberOfScheduledPrograms > 0 && numberOfActivePrograms <= 0 )
        {
            if( numberOfScheduledPrograms < _lmprograms.size() )
            {
                setControlAreaState(CtiLMControlArea::PartiallyScheduledState);
            }
            else
            {
                setControlAreaState(CtiLMControlArea::FullyScheduledState);
            }
        }
        else
        {
            setControlAreaState(CtiLMControlArea::PartiallyActiveState);
        }
    }

    return returnBOOL;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Takes appropriate action for a manual control messages.
---------------------------------------------------------------------------*/
void CtiLMControlArea::handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    LONG numberOfScheduledPrograms = 0;
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getManualControlReceivedFlag() )
        {
            if( currentLMProgram->handleManualControl(currentTime,multiPilMsg,multiDispatchMsg, multiNotifMsg) )
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
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ScheduledState )
        {
            numberOfScheduledPrograms++;
        }
    }

    if( numberOfFullyActivePrograms > 0 &&
        numberOfFullyActivePrograms == _lmprograms.size() )
    {
        if( getControlAreaState() != CtiLMControlArea::FullyActiveState )
        {
            setControlAreaState(CtiLMControlArea::FullyActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                string text("Manual Start, LM Control Area: ");
                text += getPAOName();
                string additional("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            }
            setUpdatedFlag(TRUE);
        }
    }
    else if( numberOfActivePrograms == 0 && numberOfScheduledPrograms <= 0 )
    {
        setControlAreaState(CtiLMControlArea::InactiveState);
        if( previousControlAreaState != CtiLMControlArea::InactiveState )
        {
            string text("Manual Stop, LMControl Area: ");
            text += getPAOName();
            string additional("");
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);
            CTILOG_INFO(dout, text << ", " << additional);
            setCurrentStartPriority(-1);
            setUpdatedFlag(TRUE);
        }
    }
    else if( numberOfScheduledPrograms > 0 && numberOfActivePrograms <= 0 )
    {
        if( numberOfScheduledPrograms < _lmprograms.size() )
        {
            setControlAreaState(CtiLMControlArea::PartiallyScheduledState);
        }
        else
        {
            setControlAreaState(CtiLMControlArea::FullyScheduledState);
        }
        // This is really a state that is set up outside of this function so we are not logging this behavior.
    }
    else
    {
        if( getControlAreaState() != CtiLMControlArea::PartiallyActiveState )
        {
            setControlAreaState(CtiLMControlArea::PartiallyActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                string text("Manual Start, LM Control Area: ");
                text += getPAOName();
                string additional("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
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
void CtiLMControlArea::handleTimeBasedControl(CtiTime currentTime, LONG secondsFromBeginningOfDay, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{

    // for all timed programs
    // if active and not in a control windows
    // then stop the program
    // if !active and in a control window
    // then start the program

    LONG previousControlAreaState = getControlAreaState();
    LONG numberOfActivePrograms = 0;
    LONG numberOfFullyActivePrograms = 0;
    LONG numberOfScheduledPrograms = 0;
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];

        if( currentLMProgram->getControlType() == CtiLMProgramBase::TimedType )
        {
            if( currentLMProgram->handleTimedControl(currentTime, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg, multiNotifMsg) )
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
        else if( currentLMProgram->getProgramState() == CtiLMProgramBase::ScheduledState )
        {
            numberOfScheduledPrograms++;
        }
    }

    if( numberOfFullyActivePrograms > 0 &&
        numberOfFullyActivePrograms == _lmprograms.size() )
    {
        if( getControlAreaState() != CtiLMControlArea::FullyActiveState )
        {
            setControlAreaState(CtiLMControlArea::FullyActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                string text("Timed Start, LM Control Area: ");
                text += getPAOName();
                string additional("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
            }
            setUpdatedFlag(TRUE);
        }
    }
    else if( numberOfActivePrograms == 0 )
    {
        if( previousControlAreaState == CtiLMControlArea::PartiallyActiveState ||
            previousControlAreaState == CtiLMControlArea::FullyActiveState )
        {
            string text("Timed Stop, LMControl Area: ");
            text += getPAOName();
            string additional("");
            CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
            signal->setSOE(1);

            multiDispatchMsg->insert(signal);
            CTILOG_INFO(dout, text << ", " << additional);

            // This is different than the manual control. Time based control does not currently schedule while manual does.
            // In the case of timed control we still want the timed stop message even though something is scheduled.
            if( numberOfScheduledPrograms >= _lmprograms.size() )
            {
                setControlAreaState(CtiLMControlArea::FullyScheduledState);
            }
            else if( numberOfScheduledPrograms > 0 )
            {
                setControlAreaState(CtiLMControlArea::PartiallyScheduledState);
            }
            else
            {
                setControlAreaState(CtiLMControlArea::InactiveState);
            }

            setCurrentStartPriority(-1);
            setUpdatedFlag(TRUE);
        }
    }
    else
    {
        if( getControlAreaState() != CtiLMControlArea::PartiallyActiveState &&
            getControlAreaState() != CtiLMControlArea::FullyActiveState )
        {
            setControlAreaState(CtiLMControlArea::PartiallyActiveState);
            if( previousControlAreaState == CtiLMControlArea::InactiveState )
            {
                string text("Timed Start, LM Control Area: ");
                text += getPAOName();
                string additional("");
                CtiSignalMsg* signal = CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent);
                signal->setSOE(1);

                multiDispatchMsg->insert(signal);
                CTILOG_INFO(dout, text << ", " << additional);
                setUpdatedFlag(TRUE);
            }
        }
    }
}

/*----------------------------------------------------------------------------
  handleNotification

  Send out any necessary notifications

  This is periodically called for each active Control Area (every 15 seconds or so).

----------------------------------------------------------------------------*/
void CtiLMControlArea::handleNotification(CtiTime currentTime, CtiMultiMsg* multiNotifMsg)
{
    for( LONG i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgram = _lmprograms[i];
        if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
        {
            CtiLMProgramDirectSPtr currentLMDirectProgram = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram);

            // Notify of activation
            if( currentLMDirectProgram->getNotifyActiveTime() > gInvalidCtiTime &&
                currentLMDirectProgram->getNotifyActiveTime() <= currentTime )
            {
                currentLMDirectProgram->notifyGroupsOfStart(multiNotifMsg);
                currentLMDirectProgram->setNotifyActiveTime(gInvalidCtiTime);
                currentLMDirectProgram->dumpDynamicData();
            }

            if( currentLMDirectProgram->isAdjustNotificationPending() )
            {
                currentLMDirectProgram->notifyGroupsOfAdjustment(multiNotifMsg);
            }

            //Notify of inactivation
            if( currentLMDirectProgram->getNotifyInactiveTime() > gInvalidCtiTime &&
                currentLMDirectProgram->getNotifyInactiveTime() <= currentTime )
            {
                currentLMDirectProgram->notifyGroupsOfStop(multiNotifMsg);
                currentLMDirectProgram->setNotifyInactiveTime(gInvalidCtiTime);
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
        if( getControlAreaState() == CtiLMControlArea::PartiallyActiveState ||
            getControlAreaState() == CtiLMControlArea::FullyActiveState ||
            getControlAreaState() == CtiLMControlArea::ManualActiveState )//controlling
        {
            multiDispatchMsg->insert(CTIDBG_new CtiPointDataMsg(getControlAreaStatusPointId(),STATEONE,NormalQuality,StatusPointType));
        }
        else//not controlling
        {
            multiDispatchMsg->insert(CTIDBG_new CtiPointDataMsg(getControlAreaStatusPointId(),STATEZERO,NormalQuality,StatusPointType));
        }
    }

    for( long i=0;i<_lmprograms.size();i++ )
    {
        CtiLMProgramBaseSPtr currentLMProgramBase = _lmprograms[i];
        currentLMProgramBase->createControlStatusPointUpdates(multiDispatchMsg);
    }
}

// Updates the CA state based on the programs under this control area.
// Note this does not set the inactive state. The reason it does not is due to
// the attempting control state which is beyond the calculations done here but only
// exists when there is no active or scheduled programs.
void CtiLMControlArea::updateStateFromPrograms()
{
    LONG fullyActivePrograms = 0;
    LONG activePrograms = 0;
    LONG scheduledPrograms = 0;

    for each( CtiLMProgramBaseSPtr program in _lmprograms )
    {
        if( program->getProgramState() == CtiLMProgramBase::FullyActiveState ||
            program->getProgramState() == CtiLMProgramBase::ManualActiveState ||
            program->getProgramState() == CtiLMProgramBase::TimedActiveState )
        {
            fullyActivePrograms++;
            activePrograms++;
        }
        else if( program->getProgramState() == CtiLMProgramBase::ActiveState )
        {
            activePrograms++;
        }
        else if( program->getProgramState() == CtiLMProgramBase::ScheduledState )
        {
            scheduledPrograms++;
        }
    }

    if( fullyActivePrograms > 0 &&
        fullyActivePrograms >= _lmprograms.size() )
    {
        setControlAreaState(CtiLMControlArea::FullyActiveState);
    }
    else if( activePrograms > 0 )
    {
        setControlAreaState(CtiLMControlArea::PartiallyActiveState);
    }
    else if( scheduledPrograms > 0 )
    {
        if( scheduledPrograms < _lmprograms.size() )
        {
            setControlAreaState(CtiLMControlArea::PartiallyScheduledState);
        }
        else
        {
            setControlAreaState(CtiLMControlArea::FullyScheduledState);
        }
    }
}

/*----------------------------------------------------------------------------
  updateTimedPrograms

  Updates the start/stop time of all the timed programs in this control
  area based on both program and control area windows

  Now also schedules start and stop notifications.

  If a manual control was received, this will not override it.
----------------------------------------------------------------------------*/
void CtiLMControlArea::updateTimedPrograms(LONG secondsFromBeginningOfDay)
{
    for( int i = 0; i < _lmprograms.size(); i++ )
    {
        CtiLMProgramBaseSPtr lm_program = _lmprograms[i];
        if( lm_program->getPAOType() == TYPE_LMPROGRAM_DIRECT &&
            lm_program->getControlType() == CtiLMProgramBase::TimedType )
        {
            CtiLMProgramDirectSPtr lm_direct = boost::static_pointer_cast< CtiLMProgramDirect >(lm_program);

            CtiTime resultStart, resultStop;

            CtiTime beginTime = GetTimeFromOffsetAndDate(secondsFromBeginningOfDay, CtiDate());

            if( !FitTimeToWindows(beginTime, gEndOfCtiTime, resultStart, resultStop, this, lm_program) )
            {
                // If we cant find the next window, this generally means our windows are
                // messed up and no control will ever happen. Tell client there is a problem!
                resultStart = CtiTime::neg_infin;
                resultStop = CtiTime::neg_infin;
            }

            if( lm_direct->getManualControlReceivedFlag() ||
                lm_direct->getDirectStopTime() == resultStop )
            {
                continue;
            }

            if( ! lm_direct->getConstraintOverride() )
            {
                CtiLMProgramConstraintChecker con_checker{*lm_direct, beginTime};

                if( ! con_checker.checkAutomaticProgramConstraints(resultStart, resultStop) )
                {
                    if( _LM_DEBUG & LM_DEBUG_CONSTRAINTS )
                    {
                        if( const boost::optional<std::string> violations = con_checker.dumpViolations() )
                        {
                            CTILOG_DEBUG(dout, *violations);
                        }
                    }

                    continue;
                }
            }

            lm_direct->setDirectStartTime(resultStart);
            lm_direct->setDirectStopTime(resultStop);

            if( lm_direct->isControlling() )
            {   // If we are controlling already, we dont want to send another start message. This happens
                // when the control window is moved around.
                lm_direct->scheduleStopNotificationForTimedControl(resultStop);
            }
            else
            {
                lm_direct->scheduleNotificationForTimedControl(resultStart, resultStop);
            }

            setUpdatedFlag(TRUE);
        }
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMControlArea::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMControlArea::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( isDirty() )
    {
        if( !_insertDynamicDataFlag )
        {
            static const std::string sql_update = "update dynamiclmcontrolarea"
                                                  " set "
                                                    "nextchecktime = ?, "
                                                    "newpointdatareceivedflag = ?, "
                                                    "updatedflag = ?, "
                                                    "controlareastate = ?, "
                                                    "currentpriority = ?, "
                                                    "timestamp = ?, "
                                                    "currentdailystarttime = ?, "
                                                    "currentdailystoptime = ?"
                                                  " where "
                                                    "deviceid = ?";

            Cti::Database::DatabaseWriter   updater(conn, sql_update);

            updater
                << getNextCheckTime()
                << ( getNewPointDataReceivedFlag() ? std::string("Y") : std::string("N") )
                << ( getUpdatedFlag() ? std::string("Y") : std::string("N") )
                << getControlAreaState()
                << getCurrentStartPriority()
                << currentDateTime
                << _currentdailystarttime
                << _currentdailystoptime
                << getPAOId();

            if( Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB)) )
            {
                setDirty(false); // No error occurred!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted control area into DynamicLMControlArea: " << getPAOName());

            static const std::string sql_insert = "insert into dynamiclmcontrolarea values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter   inserter(conn, sql_insert);

            inserter
                << getPAOId()
                << getNextCheckTime()
                << ( getNewPointDataReceivedFlag() ? std::string("Y") : std::string("N") )
                << ( getUpdatedFlag() ? std::string("Y") : std::string("N") )
                << getControlAreaState()
                << getCurrentStartPriority()
                << currentDateTime
                << _currentdailystarttime
                << _currentdailystoptime;

            if( Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB)) )
            {
                _insertDynamicDataFlag = false;
                setDirty(false); // No error occurred!
            }
        }
    }

    for each( CtiLMControlAreaTrigger *currentTrigger in _lmcontrolareatriggers )
    {
        currentTrigger->dumpDynamicData();
    }
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
        _paoType = right._paoType;
        _paoTypeString = right._paoTypeString;
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

        delete_container(_lmcontrolareatriggers);
        _lmcontrolareatriggers.clear();
        for( LONG i=0;i<right._lmcontrolareatriggers.size();i++ )
        {
            _lmcontrolareatriggers.push_back(((CtiLMControlAreaTrigger*)right._lmcontrolareatriggers[i])->replicate());
        }

        _lmprograms.clear();
        for( LONG j=0;j<right._lmprograms.size();j++ )
        {
            _lmprograms.push_back(CtiLMProgramBaseSPtr((right._lmprograms[j])->replicate()));
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
    return(CTIDBG_new CtiLMControlArea(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMControlArea::restore(Cti::RowReader &rdr)
{
    CtiTime dynamicTimeStamp;
    string tempBoolString;
    _insertDynamicDataFlag = FALSE;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paoTypeString;
    _paoType = resolvePAOType(_paocategory,_paoTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["defoperationalstate"] >> _defoperationalstate;
    rdr["controlinterval"] >> _controlinterval;
    rdr["minresponsetime"] >> _minresponsetime;
    rdr["defdailystarttime"] >> _defdailystarttime;
    rdr["defdailystoptime"] >> _defdailystoptime;
    rdr["requirealltriggersactiveflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setRequireAllTriggersActiveFlag(tempBoolString=="t"?TRUE:FALSE);

    setControlAreaStatusPointId(0);

    if( !rdr["pointid"].isNull() )
    {
        LONG tempPointId = 0;
        LONG tempPointOffset = 0;
        string tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;
        if( resolvePointType(tempPointType) == StatusPointType &&
            tempPointOffset == 1 )
        {
            setControlAreaStatusPointId(tempPointId);
        }
    }

    if( !rdr["nextchecktime"].isNull() )
    {
        rdr["nextchecktime"] >> _nextchecktime;
        rdr["newpointdatareceivedflag"] >> tempBoolString;
        transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        setNewPointDataReceivedFlag(tempBoolString=="y"?TRUE:FALSE);
        rdr["updatedflag"] >> tempBoolString;
        transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
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
        figureNextCheckTime(CtiTime().seconds());
        setNewPointDataReceivedFlag(FALSE);
        setUpdatedFlag(TRUE);//should always be sent to clients if it is newly added!
        setControlAreaState(CtiLMControlArea::InactiveState);
        //current priority set below zero when inactive
        setCurrentStartPriority(-1);
        resetCurrentDailyStartTime();
        resetCurrentDailyStopTime();

        _insertDynamicDataFlag = TRUE;
        setDirty(true);
    }
}

/*-----------------------------------------------------------------------------
  setDirty

  Sets the dirty flag and notifies LM that this Control Area should
  be sent to clients
-----------------------------------------------------------------------------*/
void CtiLMControlArea::setDirty(BOOL b)
{
    _CHANGED_CONTROL_AREA_LIST.insert(getPAOId());
    CtiMemDBObject::setDirty(b);
}

/*---------------------------------------------------------------------------
    getAutomaticallyStartedSignalString


---------------------------------------------------------------------------*/
string* CtiLMControlArea::getAutomaticallyStartedSignalString()
{
    string* returnString = CTIDBG_new string("");

    for( int i=0;i<_lmcontrolareatriggers.size();i++ )
    {
        CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)_lmcontrolareatriggers[i];
        if( (ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdTriggerType) ||
             ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::ThresholdPointTriggerType) ) &&
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
            *returnString += " (" + getPAOName() + ")";
        }
        else if( ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::StatusTriggerType) &&
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

    return returnString;
}


/* Public Static members */
const string CtiLMControlArea::DefOpStateEnabled = "Enabled";
const string CtiLMControlArea::DefOpStateDisabled = "Disabled";
const string CtiLMControlArea::DefOpStateNone = "None";

int CtiLMControlArea::InactiveState = STATEZERO;
int CtiLMControlArea::PartiallyActiveState = STATEONE;
int CtiLMControlArea::ManualActiveState = STATETWO;
int CtiLMControlArea::FullyScheduledState = STATETHREE;
int CtiLMControlArea::FullyActiveState = STATEFOUR;
int CtiLMControlArea::AttemptingControlState = STATEFIVE;
int CtiLMControlArea::PartiallyScheduledState = STATESIX;
