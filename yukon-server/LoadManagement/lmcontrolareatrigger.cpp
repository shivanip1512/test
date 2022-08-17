#include "precompiled.h"

#include "dbaccess.h"
#include "lmcontrolareatrigger.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "utility.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMControlAreaTrigger, CTILMCONTROLAREATRIGGER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger::CtiLMControlAreaTrigger() :
_thresholdPointId(0),
_trigger_id(0),
_paoid(0),
_triggernumber(0),
_pointid(0),
_pointvalue(0),
_normalstate(0),
_threshold(0),
_projectionpoints(0),
_projectaheadduration(0),
_thresholdkickpercent(0),
_minrestoreoffset(0),
_peakpointid(0),
_peakpointvalue(0),
_projectedpointvalue(0),
_insertDynamicDataFlag(false)
{
}

CtiLMControlAreaTrigger::CtiLMControlAreaTrigger(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMControlAreaTrigger::CtiLMControlAreaTrigger(const CtiLMControlAreaTrigger& lmcontrolareatrigger)
{
    operator=(lmcontrolareatrigger);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger::~CtiLMControlAreaTrigger()
{
    _projectionpointentriesqueue.clear();
}

/*---------------------------------------------------------------------------
    getThresholdPointId

    Returns the unique point ID of the "Threshold Point".
---------------------------------------------------------------------------*/
long CtiLMControlAreaTrigger::getThresholdPointId() const
{
    return _thresholdPointId;
}

/*---------------------------------------------------------------------------
    getTriggerId

    Returns the unique id of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getTriggerId() const
{
    return _trigger_id;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id the control area this trigger is attached to
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getTriggerNumber

    Returns the trigger number of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getTriggerNumber() const
{

    return _triggernumber;
}

/*---------------------------------------------------------------------------
    getTriggerType

    Returns the trigger type of the trigger
---------------------------------------------------------------------------*/
const string& CtiLMControlAreaTrigger::getTriggerType() const
{

    return _triggertype;
}

/*---------------------------------------------------------------------------
    getPointId

    Returns the point id of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getPointId() const
{

    return _pointid;
}

/*---------------------------------------------------------------------------
    getPointValue

    Returns the point value of the trigger
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlAreaTrigger::getPointValue() const
{

    return _pointvalue;
}

/*---------------------------------------------------------------------------
    getLastPointValueTimestamp

    Returns the timestamp of the last point value received by the trigger
---------------------------------------------------------------------------*/
const CtiTime& CtiLMControlAreaTrigger::getLastPointValueTimestamp() const
{

    return _lastpointvaluetimestamp;
}

/*---------------------------------------------------------------------------
    getNormalState

    Returns the raw state of the normal state of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getNormalState() const
{

    return _normalstate;
}

/*---------------------------------------------------------------------------
    getThreshold

    Returns the threshold of the trigger
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlAreaTrigger::getThreshold() const
{

    return _threshold;
}

/*---------------------------------------------------------------------------
    getProjectionType

    Returns the projection type of the trigger
---------------------------------------------------------------------------*/
const string& CtiLMControlAreaTrigger::getProjectionType() const
{

    return _projectiontype;
}

/*---------------------------------------------------------------------------
    getProjectionPoints

    Returns the number previous points to include in the projection
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getProjectionPoints() const
{

    return _projectionpoints;
}

/*---------------------------------------------------------------------------
    getProjectAheadDuration

    Returns how far out to do the projection in seconds
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getProjectAheadDuration() const
{

    return _projectaheadduration;
}

/*---------------------------------------------------------------------------
    getThresholdKickPercent

    Returns the threshold kick percentage of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getThresholdKickPercent() const
{

    return _thresholdkickpercent;
}

/*---------------------------------------------------------------------------
    getMinRestoreOffset

    Returns the minimum restore offset of the trigger
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlAreaTrigger::getMinRestoreOffset() const
{

    return _minrestoreoffset;
}

/*---------------------------------------------------------------------------
    getPeakPointId

    Returns the peak point id of the trigger
---------------------------------------------------------------------------*/
LONG CtiLMControlAreaTrigger::getPeakPointId() const
{


    return _peakpointid;
}

/*---------------------------------------------------------------------------
    getPeakPointValue

    Returns the peak point value of the trigger
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlAreaTrigger::getPeakPointValue() const
{

    return _peakpointvalue;
}

/*---------------------------------------------------------------------------
    getLastPeakPointValueTimestamp

    Returns the timestamp of the last peak point value received by the trigger
---------------------------------------------------------------------------*/
const CtiTime& CtiLMControlAreaTrigger::getLastPeakPointValueTimestamp() const
{

    return _lastpeakpointvaluetimestamp;
}

/*---------------------------------------------------------------------------
    getProjectedPointValue

    Returns the peak point value of the trigger
---------------------------------------------------------------------------*/
DOUBLE CtiLMControlAreaTrigger::getProjectedPointValue() const
{

    return _projectedpointvalue;
}

/*---------------------------------------------------------------------------
    getProjectionPointEntriesQueue

    Returns the timestamp of the last peak point value received by the trigger
---------------------------------------------------------------------------*/
std::vector<CtiLMProjectionPointEntry>& CtiLMControlAreaTrigger::getProjectionPointEntriesQueue()
{

    return _projectionpointentriesqueue;
}

/*---------------------------------------------------------------------------
    setThresholdPointId

    Sets the "Threshold Point" trigger types point ID.
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setThresholdPointId(const long thresholdId)
{
    _thresholdPointId = thresholdId;
    return *this;
}

/*---------------------------------------------------------------------------
    setTriggerId

    Sets the id of the trigger - know why you are calling this
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setTriggerId(LONG id)
{
    _trigger_id = id;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the id of the control area - use with caution
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setTriggerNumber

    Sets the trigger number of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setTriggerNumber(LONG trignum)
{

    _triggernumber = trignum;
    return *this;
}

/*---------------------------------------------------------------------------
    setTriggerType

    Sets the trigger type of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setTriggerType(const string& trigtype)
{

    _triggertype = trigtype;
    return *this;
}

/*---------------------------------------------------------------------------
    setPointId

    Sets the point id of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setPointId(LONG pntid)
{

    _pointid = pntid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPointValue

    Sets the point value of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setPointValue(DOUBLE pntval)
{

    _pointvalue = pntval;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastPointValueTimestamp

    Sets the timestamp of the last point value received by the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setLastPointValueTimestamp(const CtiTime& lastvaltime)
{

    _lastpointvaluetimestamp = lastvaltime;
    return *this;
}

/*---------------------------------------------------------------------------
    setNormalState

    Sets the raw state of the normal state of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setNormalState(LONG normalst)
{

    _normalstate = normalst;
    return *this;
}

/*---------------------------------------------------------------------------
    setThreshold

    Sets the Threshold of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setThreshold(DOUBLE threshold)
{

    _threshold = threshold;
    return *this;
}

/*---------------------------------------------------------------------------
    setProjectionType

    Sets the projection type of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setProjectionType(const string& projtype)
{

    _projectiontype = projtype;
    return *this;
}

/*---------------------------------------------------------------------------
    setProjectionPoints

    Sets the projection points of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setProjectionPoints(LONG projpoints)
{

    _projectionpoints = projpoints;
    return *this;
}

/*---------------------------------------------------------------------------
    setProjectAheadDuration

    Sets the number of seconds ahead to do the projection of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setProjectAheadDuration(LONG projaheaddur)
{

    _projectaheadduration = projaheaddur;
    return *this;
}

/*---------------------------------------------------------------------------
    setThresholdKickPercent

    Sets the threshold kick up percentage of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setThresholdKickPercent(LONG threskickpercent)
{

    _thresholdkickpercent = threskickpercent;
    return *this;
}

/*---------------------------------------------------------------------------
    setMinRestoreOffset

    Sets the minimum restore offset of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setMinRestoreOffset(DOUBLE minrestoffset)
{

    _minrestoreoffset = minrestoffset;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakPointId

    Sets the peak point id of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setPeakPointId(LONG peakptid)
{

    _peakpointid = peakptid;
    return *this;
}

/*---------------------------------------------------------------------------
    setPeakPointValue

    Sets the peak point value of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setPeakPointValue(DOUBLE peakptval)
{

    _peakpointvalue = peakptval;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastPeakPointValueTimestamp

    Sets the timestamp of the last peak point value received by the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setLastPeakPointValueTimestamp(const CtiTime& lastpeakvaltime)
{

    _lastpeakpointvaluetimestamp = lastpeakvaltime;
    return *this;
}

/*---------------------------------------------------------------------------
    setProjectedPointValue

    Sets the peak point value of the trigger
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setProjectedPointValue(DOUBLE proptval)
{

    _projectedpointvalue = proptval;
    return *this;
}


/*-------------------------------------------------------------------------
    calculateProjectedValue


--------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::calculateProjectedValue()
{
    if( !ciStringEqual(getProjectionType(), CtiLMControlAreaTrigger::NoneProjectionType) )
    {
        if( ciStringEqual(getProjectionType(), CtiLMControlAreaTrigger::LSFProjectionType ) )
        {
            while( getProjectionPointEntriesQueue().size() > getProjectionPoints() )//trim excess pointvalues
            {
                getProjectionPointEntriesQueue().erase(getProjectionPointEntriesQueue().begin());
            }

            if( getProjectionPointEntriesQueue().size() >= getProjectionPoints() && getProjectionPoints() > 0 &&
                getProjectionPointEntriesQueue()[getProjectionPoints()-1].getTimestamp().seconds() > (CtiTime::now().seconds() - 60*60) )
            {
                double x       = 0.0;
                double sumX    = 0.0;
                double sumY    = 0.0;
                double sumXX   = 0.0;
                double sumXY   = 0.0;

                double dtemp;
                double delta;
                double sigmaY2;

                for( int i=0;i<getProjectionPointEntriesQueue().size();i++ )
                {
                    x = getProjectionPointEntriesQueue()[i].getTimestamp().seconds() - getProjectionPointEntriesQueue()[0].getTimestamp().seconds();

                    sumX += (double)x;
                    sumY += getProjectionPointEntriesQueue()[i].getValue();
                    sumXX += ((double)(x * x));
                    sumXY += (((double)x) * getProjectionPointEntriesQueue()[i].getValue());
                }

                delta = (getProjectionPointEntriesQueue().size() * sumXX) - (sumX * sumX);

                if( delta != 0.0 )
                {
                    double slope = ( (getProjectionPointEntriesQueue().size() * sumXY) - (sumX * sumY) ) / delta;
                    double intersect = ( (sumXX * sumY) - (sumX * sumXY) ) / delta;
                    double inputX = getProjectionPointEntriesQueue()[getProjectionPointEntriesQueue().size()-1].getTimestamp().seconds() - getProjectionPointEntriesQueue()[0].getTimestamp().seconds() + getProjectAheadDuration();
                    double tempProjectedValue = (slope*inputX)+intersect;
                    setProjectedPointValue(tempProjectedValue);

                    CTILOG_INFO(dout, "trigger ID " << getTriggerId() << " projected value: " << tempProjectedValue);
                }
            }
            else
            {
                CTILOG_INFO(dout, "Not enough getProjectionPointEntriesQueue().entries(): " << getProjectionPointEntriesQueue().size() << " need getProjectionPoints(): " << getProjectionPoints());
                if( getProjectionPointEntriesQueue().size() > 0 )
                {
                    setProjectedPointValue(getProjectionPointEntriesQueue()[0].getValue());
                }

            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unknown Projection Type: " << getProjectionType() << " for trigger ID " << getTriggerId());
        }
    }
}

/*-----------------------------------------------------------------------------
  hasReceviedPointData

  Returns true if at some point after creation we have received some point
  data.
-----------------------------------------------------------------------------*/
bool CtiLMControlAreaTrigger::hasReceivedPointData() const
{
    return _lastpointvaluetimestamp > gInvalidCtiTime;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::operator=(const CtiLMControlAreaTrigger& right)
{
    if( this != &right )
    {
        _thresholdPointId = right._thresholdPointId;
        _trigger_id = right._trigger_id;
        _paoid = right._paoid;
        _triggernumber = right._triggernumber;
        _triggertype = right._triggertype;
        _pointid = right._pointid;
        _pointvalue = right._pointvalue;
        _lastpointvaluetimestamp = right._lastpointvaluetimestamp;
        _normalstate = right._normalstate;
        _threshold = right._threshold;
        _projectiontype = right._projectiontype;
        _projectionpoints = right._projectionpoints;
        _projectaheadduration = right._projectaheadduration;
        _thresholdkickpercent = right._thresholdkickpercent;
        _minrestoreoffset = right._minrestoreoffset;
        _peakpointid = right._peakpointid;
        _peakpointvalue = right._peakpointvalue;
        _lastpeakpointvaluetimestamp = right._lastpeakpointvaluetimestamp;
        _projectedpointvalue = right._projectedpointvalue;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMControlAreaTrigger::operator==(const CtiLMControlAreaTrigger& right) const
{
    return getTriggerId() == right.getTriggerId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMControlAreaTrigger::operator!=(const CtiLMControlAreaTrigger& right) const
{
    return !operator==(right);
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger* CtiLMControlAreaTrigger::replicate() const
{
    return(CTIDBG_new CtiLMControlAreaTrigger(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::restore(Cti::RowReader &rdr)
{

    _insertDynamicDataFlag = FALSE;

    rdr["triggerid"] >> _trigger_id;
    rdr["deviceid"] >> _paoid;//will be paobjectid
    rdr["triggernumber"] >> _triggernumber;
    rdr["triggertype"] >> _triggertype;
    rdr["pointid"] >> _pointid;
    rdr["normalstate"] >> _normalstate;
    rdr["threshold"] >> _threshold;
    rdr["projectiontype"] >> _projectiontype;
    rdr["projectionpoints"] >> _projectionpoints;
    rdr["projectaheadduration"] >> _projectaheadduration;
    rdr["thresholdkickpercent"] >> _thresholdkickpercent;
    rdr["minrestoreoffset"] >> _minrestoreoffset;
    rdr["peakpointid"] >> _peakpointid;
    rdr["ThresholdPointId"] >> _thresholdPointId;

    setProjectedPointValue(0.0);
    if( !rdr["pointvalue"].isNull() )
    {
        rdr["pointvalue"] >> _pointvalue;
        rdr["lastpointvaluetimestamp"] >> _lastpointvaluetimestamp;
        rdr["peakpointvalue"] >> _peakpointvalue;
        rdr["lastpeakpointvaluetimestamp"] >> _lastpeakpointvaluetimestamp;

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        setPointValue(0.0);
        setLastPointValueTimestamp(gInvalidCtiTime);
        setPeakPointValue(0.0);
        setLastPeakPointValueTimestamp(gInvalidCtiTime);

        _insertDynamicDataFlag = TRUE;
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( !_insertDynamicDataFlag )
    {
        static const std::string sql_update = "update dynamiclmcontrolareatrigger"
                                              " set "
                                                "pointvalue = ?, "
                                                "lastpointvaluetimestamp = ?, "
                                                "peakpointvalue = ?, "
                                                "lastpeakpointvaluetimestamp = ?"
                                              " where "
                                                "triggerid = ?";

        Cti::Database::DatabaseWriter   updater(conn, sql_update);

        updater
            << getPointValue()
            << getLastPointValueTimestamp()
            << getPeakPointValue()
            << getLastPeakPointValueTimestamp()
            << getTriggerId();

        Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB));
    }
    else
    {
        CTILOG_INFO(dout, "Inserted control area trigger into DynamicLMControlAreaTrigger PAO ID: " << getPAOId() << " TriggerNumber: " << getTriggerNumber());

        static const std::string sql_insert = "insert into dynamiclmcontrolareatrigger values (?, ?, ?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter   inserter(conn, sql_insert);

        inserter
            << getPAOId()
            << getTriggerNumber()
            << getPointValue()
            << getLastPointValueTimestamp()
            << getPeakPointValue()
            << getLastPeakPointValueTimestamp()
            << getTriggerId();

        if( Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB)) )
        {
            _insertDynamicDataFlag = false; // Insert successful!
        }
    }
}



/* Public Static members */
const string CtiLMControlAreaTrigger::ThresholdTriggerType = "Threshold";
const string CtiLMControlAreaTrigger::ThresholdPointTriggerType = "Threshold Point";
const string CtiLMControlAreaTrigger::StatusTriggerType = "Status";

const string CtiLMControlAreaTrigger::NoneProjectionType = "(none)";
const string CtiLMControlAreaTrigger::LSFProjectionType = "LSF";


//*************************************************************
//**********  CtiLMProjectionPointEntry              **********
//**********                                         **********
//**********  This is equivalent to an inner class,  **********
//**********  only used for projections              **********
//*************************************************************
CtiLMProjectionPointEntry::CtiLMProjectionPointEntry(double val, const CtiTime& time)
{
    setValue(val);
    setTimestamp(time);
}

CtiLMProjectionPointEntry::CtiLMProjectionPointEntry(const CtiLMProjectionPointEntry& pointEntry)
{
    operator=(pointEntry);
}

CtiLMProjectionPointEntry::~CtiLMProjectionPointEntry()
{
}

double CtiLMProjectionPointEntry::getValue() const
{

    return _value;
}
const CtiTime& CtiLMProjectionPointEntry::getTimestamp() const
{

    return _timestamp;
}

CtiLMProjectionPointEntry& CtiLMProjectionPointEntry::setValue(double val)
{

    _value = val;
    return *this;
}
CtiLMProjectionPointEntry& CtiLMProjectionPointEntry::setTimestamp(const CtiTime& time)
{

    _timestamp = time;
    return *this;
}

CtiLMProjectionPointEntry& CtiLMProjectionPointEntry::operator=(const CtiLMProjectionPointEntry& right)
{


    if( this != &right )
    {
        _value = right.getValue();
        _timestamp = right.getTimestamp();
    }

    return *this;
}


