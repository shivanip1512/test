/*---------------------------------------------------------------------------
        Filename:  lmcontrolareatrigger.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMControlAreaTrigger.
                        CtiLMControlAreaTrigger maintains the state and handles
                        the persistence of control area triggers in Load
                        Management.

        Initial Date:  8/18/2000

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmcontrolareatrigger.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMControlAreaTrigger, CTILMCONTROLAREATRIGGER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger::CtiLMControlAreaTrigger()
{
}

CtiLMControlAreaTrigger::CtiLMControlAreaTrigger(RWDBReader& rdr)
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
    getPAOId

    Returns the unique id of the trigger
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
const RWCString& CtiLMControlAreaTrigger::getTriggerType() const
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
const RWDBDateTime& CtiLMControlAreaTrigger::getLastPointValueTimestamp() const
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
const RWCString& CtiLMControlAreaTrigger::getProjectionType() const
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
const RWDBDateTime& CtiLMControlAreaTrigger::getLastPeakPointValueTimestamp() const
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
RWTValDlist<CtiLMProjectionPointEntry>& CtiLMControlAreaTrigger::getProjectionPointEntriesQueue()
{

    return _projectionpointentriesqueue;
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
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setTriggerType(const RWCString& trigtype)
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
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setLastPointValueTimestamp(const RWDBDateTime& lastvaltime)
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
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setProjectionType(const RWCString& projtype)
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
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::setLastPeakPointValueTimestamp(const RWDBDateTime& lastpeakvaltime)
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
    //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
    if( getProjectionType().compareTo(CtiLMControlAreaTrigger::NoneProjectionType,RWCString::ignoreCase) )
    {
        if( !getProjectionType().compareTo(CtiLMControlAreaTrigger::LSFProjectionType,RWCString::ignoreCase) )
        {
            if( getProjectionPointEntriesQueue().entries() >= getProjectionPoints() )
            {
                while( getProjectionPointEntriesQueue().entries() > getProjectionPoints() )
                {//trim excess pointvalues
                    getProjectionPointEntriesQueue().get();
                }

                double x       = 0.0;
                double sumX    = 0.0;
                double sumY    = 0.0;
                double sumXX   = 0.0;
                double sumXY   = 0.0;

                double dtemp;
                double delta;
                double sigmaY2;

                for(int i=0;i<getProjectionPointEntriesQueue().entries();i++)
                {
                    x = getProjectionPointEntriesQueue()[i].getTimestamp().seconds() - getProjectionPointEntriesQueue()[0].getTimestamp().seconds();

                    sumX += (double)x;
                    sumY += getProjectionPointEntriesQueue()[i].getValue();
                    sumXX += ((double)(x * x));
                    sumXY += (((double)x) * getProjectionPointEntriesQueue()[i].getValue());
                }

                delta = (getProjectionPointEntriesQueue().entries() * sumXX) - (sumX * sumX);

                if(delta != 0.0)
                {
                    double slope = ( (getProjectionPointEntriesQueue().entries() * sumXY) - (sumX * sumY) ) / delta;
                    double intersect = ( (sumXX * sumY) - (sumX * sumXY) ) / delta;
                    double inputX = getProjectionPointEntriesQueue()[getProjectionPointEntriesQueue().entries()-1].getTimestamp().seconds() - getProjectionPointEntriesQueue()[0].getTimestamp().seconds() + getProjectAheadDuration();
                    double tempProjectedValue = (slope*inputX)+intersect;
                    setProjectedPointValue(tempProjectedValue);

                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - calculateProjectedValue() debug info: " << endl;
                        /*dout << " - ProjectionPointEntriesQueue entries: " << endl;
                        for(int j=0;j<getProjectionPointEntriesQueue().entries();j++)
                        {
                            dout << " Entry number: " << j << " value: " << getProjectionPointEntriesQueue()[j].getValue() << " timestamp: " << getProjectionPointEntriesQueue()[j].getTimestamp() << endl;
                        }
                        dout << " x: " << x << endl;
                        dout << " delta: " << delta << endl;
                        dout << " sumX: " << sumX << endl;
                        dout << " sumY: " << sumY << endl;
                        dout << " sumXX: " << sumXX << endl;
                        dout << " sumXY: " << sumXY << endl;
                        dout << " Slope: " << slope << endl;
                        dout << " Intercept: " << intersect << endl;
                        dout << " Input x: " << inputX << endl;*/
                        dout << " Projected Value: " << tempProjectedValue << endl;
                    }
                    //From Corey's projection method, don't think we need all this
                    /*long secondsFromBeginningOfRegression = 0;
                    for(int i=0;i<getProjectionPointEntriesQueue().entries();i++)
                    {
                        dtemp = ( getProjectionPointEntriesQueue()[i].getValue() - intersect - slope * secondsFromBeginningOfRegression);
                        sigmaY2 += (dtemp * dtemp);
                        if( i+1 < getProjectionPointEntriesQueue().entries() )
                        {
                            secondsFromBeginningOfRegression += getProjectionPointEntriesQueue()[i+1].getTimestamp().seconds() - getProjectionPointEntriesQueue()[i].getTimestamp().seconds();
                        }
                    }
                    sigmaY2 = sigmaY2 / (x - 2);
                    if(sigmaY2 > 0.0)
                    {
                        regress->sigma_y = sqrt( sigmaY2 );
                    }
                    else
                    {
                       regress->sigma_y = 0;
                    }
                    dtemp = x * sigmaY2 / delta;
                    if(dtemp > 0.0)
                    {
                       regress->sigma_m = sqrt( dtemp );
                    }
                    else
                    {
                       regress->sigma_m = 0.0;
                    }
                    dtemp = sigmaY2 * sumXX / delta;
                    if(dtemp > 0.0)
                    {
                       regress->sigma_b = sqrt( dtemp );
                    }
                    else
                    {
                       regress->sigma_b = 0.0;
                    }*/
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Not enough getProjectionPointEntriesQueue().entries(): " << getProjectionPointEntriesQueue().entries() << " need getProjectionPoints(): " << getProjectionPoints() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unknown Projection Type: " << getProjectionType() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    RWTime tempTime1;
    RWTime tempTime2;

    istrm >> _paoid
    >> _triggernumber
    >> _triggertype
    >> _pointid
    >> _pointvalue
    >> tempTime1
    >> _normalstate
    >> _threshold
    >> _projectiontype
    >> _projectionpoints
    >> _projectaheadduration
    >> _thresholdkickpercent
    >> _minrestoreoffset
    >> _peakpointid
    >> _peakpointvalue
    >> tempTime2
    >> _projectedpointvalue;

    _lastpointvaluetimestamp = RWDBDateTime(tempTime1);
    _lastpeakpointvaluetimestamp = RWDBDateTime(tempTime2);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _triggernumber
    << _triggertype
    << _pointid
    << _pointvalue
    << _lastpointvaluetimestamp.rwtime()
    << _normalstate
    << _threshold
    << _projectiontype
    << _projectionpoints
    << _projectaheadduration
    << _thresholdkickpercent
    << _minrestoreoffset
    << _peakpointid
    << _peakpointvalue
    << _lastpeakpointvaluetimestamp.rwtime()
    << _projectedpointvalue;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger& CtiLMControlAreaTrigger::operator=(const CtiLMControlAreaTrigger& right)
{


    if( this != &right )
    {
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

    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMControlAreaTrigger::operator!=(const CtiLMControlAreaTrigger& right) const
{

    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMControlAreaTrigger* CtiLMControlAreaTrigger::replicate() const
{
    return(new CtiLMControlAreaTrigger(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::restore(RWDBReader& rdr)
{

    RWDBNullIndicator isNull;
    _insertDynamicDataFlag = FALSE;

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

    setProjectedPointValue(0.0);
    rdr["pointvalue"] >> isNull;
    if( !isNull )
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
        setLastPointValueTimestamp(RWDBDateTime(1990,1,1,0,0,0,0));
        setPeakPointValue(0.0);
        setLastPeakPointValueTimestamp(RWDBDateTime(1990,1,1,0,0,0,0));

        _insertDynamicDataFlag = TRUE;
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this strategy.
---------------------------------------------------------------------------*/
void CtiLMControlAreaTrigger::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    {
        RWDBTable dynamicLMControlAreaTriggerTable = getDatabase().table( "dynamiclmcontrolareatrigger" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicLMControlAreaTriggerTable.updater();

            updater << dynamicLMControlAreaTriggerTable["pointvalue"].assign(getPointValue())
            << dynamicLMControlAreaTriggerTable["lastpointvaluetimestamp"].assign((RWDBDateTime)getLastPointValueTimestamp())
            << dynamicLMControlAreaTriggerTable["peakpointvalue"].assign(getPeakPointValue())
            << dynamicLMControlAreaTriggerTable["lastpeakpointvaluetimestamp"].assign((RWDBDateTime)getLastPeakPointValueTimestamp());

            updater.where( dynamicLMControlAreaTriggerTable["deviceid"]==getPAOId() &&//will be paobjectid
                           dynamicLMControlAreaTriggerTable["triggernumber"]==getTriggerNumber() );

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
                dout << RWTime() << " - Inserted control area trigger into DynamicLMControlAreaTrigger PAO ID: " << getPAOId() << " TriggerNumber: " << getTriggerNumber() << endl;
            }

            RWDBInserter inserter = dynamicLMControlAreaTriggerTable.inserter();

            inserter << getPAOId()
            << getTriggerNumber()
            << getPointValue()
            << getLastPointValueTimestamp()
            << getPeakPointValue()
            << getLastPeakPointValueTimestamp();

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



/* Public Static members */
const RWCString CtiLMControlAreaTrigger::ThresholdTriggerType = "Threshold";
const RWCString CtiLMControlAreaTrigger::StatusTriggerType = "Status";

const RWCString CtiLMControlAreaTrigger::NoneProjectionType = "None";
const RWCString CtiLMControlAreaTrigger::LSFProjectionType = "LSF";


//*************************************************************
//**********  CtiLMProjectionPointEntry              **********
//**********                                         **********
//**********  This is equivalent to an inner class,  **********
//**********  only used for projections              **********
//*************************************************************
CtiLMProjectionPointEntry::CtiLMProjectionPointEntry(double val, const RWTime& time)
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
const RWTime& CtiLMProjectionPointEntry::getTimestamp() const
{

    return _timestamp;
}

CtiLMProjectionPointEntry& CtiLMProjectionPointEntry::setValue(double val)
{

    _value = val;
    return *this;
}
CtiLMProjectionPointEntry& CtiLMProjectionPointEntry::setTimestamp(const RWTime& time)
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


