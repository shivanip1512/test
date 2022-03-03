#include "precompiled.h"

#include "calc.h"
#include "logger.h"
#include "utility.h"

#include "ctitime.h"

#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/indirected.hpp>
#include <boost/range/adaptor/transformed.hpp>

extern ULONG _CALC_DEBUG;

using namespace std;

// static const strings
const std::string CtiCalc::UpdateTypeDbStrings::Periodic   = "On Timer";
const std::string CtiCalc::UpdateTypeDbStrings::AllChange  = "On All Change";
const std::string CtiCalc::UpdateTypeDbStrings::OneChange  = "On First Change";
const std::string CtiCalc::UpdateTypeDbStrings::Historical = "Historical";
const std::string CtiCalc::UpdateTypeDbStrings::BackfillingHistorical = "Backfilling";
const std::string CtiCalc::UpdateTypeDbStrings::PeriodicPlusUpdate = "On Timer+Change";
const std::string CtiCalc::UpdateTypeDbStrings::Constant   = "Constant";

CtiCalc::CtiCalc( long pointId, const string &updateType, int updateInterval, const string &qualityFlag )
{
    _valid = TRUE;
    _pointId = pointId;
    _isBaseline = false;
    _baselineId = 0;
    _baselinePercentId = 0;

    if( ciStringEqual(qualityFlag, "y") )
    {
        //if "Y" do not calculate, always return NORMAL.
        _calculateQuality = false;
    }
    else
    {
        _calculateQuality = true;
    }

    if( (ciStringEqual(updateType,UpdateTypeDbStrings::Periodic))
        && (updateInterval > 0) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = CalcUpdateType::Periodic;
    }
    else if( ciStringEqual(updateType,UpdateTypeDbStrings::AllChange))
    {
        _updateInterval = 0;
        _updateType = CalcUpdateType::AllUpdate;
    }
    else if( ciStringEqual(updateType,UpdateTypeDbStrings::OneChange))
    {
        _updateInterval = 0;
        _updateType = CalcUpdateType::AnyUpdate;
    }
    else if( ciStringEqual(updateType,UpdateTypeDbStrings::Historical))
    {
        _updateInterval = 0;
        _updateType = CalcUpdateType::Historical;
    }
    else if( ciStringEqual(updateType, UpdateTypeDbStrings::BackfillingHistorical) )
    {
        _updateInterval = 0;
        _updateType = CalcUpdateType::BackfillingHistorical;
    }
    else if( ciStringEqual(updateType,UpdateTypeDbStrings::PeriodicPlusUpdate) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = CalcUpdateType::PeriodicPlusUpdate;
    }
    else if( ciStringEqual(updateType,UpdateTypeDbStrings::Constant) )
    {
        _updateInterval = 0;
        _updateType = CalcUpdateType::Constant;
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid Update Type: "<< updateType);
        _valid = FALSE;
    }

    if( _valid )
    {
        _pointCalcWindowEndTime = CtiTime(CtiDate(1,1,1990));
    }
}

void CtiCalc::appendComponent( std::unique_ptr<CtiCalcComponent> componentToAdd )
{
    componentToAdd->passParent( this );

    if( componentToAdd->getFunctionName() == "Baseline" )
    {
        _isBaseline = true;
        _baselineId = componentToAdd->getComponentPointId();
    }
    else if( componentToAdd->getFunctionName() == "Baseline Percent" )
    {
        _isBaseline = true;
        _baselinePercentId = componentToAdd->getComponentPointId();
    }
    else if( componentToAdd->getFunctionName() == "Regression" )
    {
        if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(componentToAdd->getComponentPointId()) )
        {
            componentPointPtr->setUseRegression();
        }
    }
    else if( componentToAdd->getFunctionName() == "Intervals To Value" ||
             componentToAdd->getFunctionName() == "Linear Slope" )
    {
        _regressionPtId = componentToAdd->getComponentPointId();
    }

    _components.emplace_back(std::move(componentToAdd));
}

void CtiCalc::clearComponentDependencies( void )
{
    for( auto& tmpComponent : _components )
    {
        if ( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(tmpComponent->getComponentPointId()) )
        {
            if( componentPointPtr->removeDependent(_pointId) == 0 )
            {//There are no dependents left, no one cares about this guy!
                CtiPointStore::remove( tmpComponent->getComponentPointId() );
            }
        }
    }
}


double CtiCalc::calculate( int &calc_quality, CtiTime &calc_time, bool &calcValid )
{
    double retVal = 0.0;
    try
    {
        //  Iterate through all of the calculations in the collection
        if( _CALC_DEBUG & CALC_DEBUG_PRECALC_VALUE )
        {
            if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(_pointId) )
            {
                CTILOG_INFO(dout, "Calc Point ID:"<< _pointId <<"; Start Value:"<< calcPointPtr->getPointValue());
            }
            else
            {
                CTILOG_ERROR(dout, "Calc Point ID:"<< _pointId <<"; Not found");
            }
        }
        _stack = {};     // Start with a blank stack.
        push( retVal );     // Prime the stack with a zero value (should effectively clear it).

        bool solidTime = false;             // If time is "solid" all components are the same time stamp.
        int componentQuality, qualityFlag = 0;
        CtiTime componentTime,
            minTime = CtiTime(YUKONEOT),
            maxTime(CtiTime::neg_infin);

        /*
         *  Iterate this calc's components passing in each succesive result (through retVal).
         */
        for( auto& tmpComponent : _components )
        {
            if( _valid &= tmpComponent->isValid( ) )  //  Entire calculation is only valid if each component is valid
            {
                retVal = tmpComponent->calculate( retVal, componentQuality, componentTime, calcValid );  //  Calculate on returned value

                qualityFlag |= (1 << componentQuality);    // Flag each returned quality...
                solidTime = calcTimeFromComponentTime( componentTime, componentQuality, minTime, maxTime );
            }
            else
            {
                break;
            }
        }

        calc_time = calcTimeFromComponentTime( minTime, maxTime );
        calc_quality = calcQualityFromComponentQuality( qualityFlag, minTime, maxTime );

        if( !_valid )   //  NOT valid - actually, you should never get here, because the ready( ) back in CalcThread should
        {
            //    detect that you're invalid, and reject you with a "not ready" then.
            CTILOG_WARN(dout, "Attempt to calculate invalid point \""<< _pointId <<"\" - returning 0.0");
            retVal = 0.0;
        }

        if( _CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE )
        {
            CTILOG_DEBUG(dout, "Calc Point ID:"<< _pointId <<"; Return Value:"<< retVal);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Calc Point ID "<< _pointId);
    }
    return retVal;
}

bool CtiCalc::push( double val )
{
    _stack.push( val );
    return( _stack.size() == 2 );    // Was this the first push (after the push(retVal = 0) stack primer)?
}


double CtiCalc::pop( void )
{
    double val;
    if( ! _stack.empty( ) )
    {
        val = _stack.top( );
        _stack.pop( );
    }
    else
    {
        CTILOG_ERROR(dout, "attempt to pop from empty stack in point \""<< _pointId <<"\" - returning 0.0 ");
        val = 0.0;
    }
    return val;
}


CalcUpdateType CtiCalc::getUpdateType( void )
{
    return _updateType;
}


BOOL CtiCalc::ready( void )
{
    BOOL isReady = TRUE;

    try
    {
        if( !_valid )
        {
            setNextInterval(getUpdateInterval());       // It is not valid, do not harp about it so often!
            isReady = FALSE;

            CTILOG_ERROR(dout, "Point "<< _pointId <<" is INVALID.");
        }
        else
        {
            switch( _updateType )
            {
            case CalcUpdateType::Periodic:
                if(CtiTime::now().seconds() > getNextInterval())
                {
                    isReady = TRUE;
                }
                else
                {
                    isReady = FALSE;
                }
                break;
            case CalcUpdateType::AllUpdate:
                for( auto& c : _components )
                {
                    isReady &= c->isUpdated( );
                }
                break;
            case CalcUpdateType::Historical:
            case CalcUpdateType::BackfillingHistorical:
            case CalcUpdateType::Constant:
                isReady = TRUE;
                break;
            case CalcUpdateType::AnyUpdate:
                for( auto& c : _components )
                {
                    isReady |= c->isUpdated( );
                    if( isReady )
                    {
                        break;
                    }
                }
                break;
            case CalcUpdateType::PeriodicPlusUpdate:
                {
                    if(CtiTime::now().seconds() >= getNextInterval())
                    {
                        for( auto& c : _components )
                        {
                            isReady &= c->isUpdated( _updateType, CtiTime(getNextInterval()) );
                        }
                    }
                    else
                    {
                        isReady = FALSE;
                    }
                    break;
                }
            }
        }

        //We think we can go, but are any of our components disabled?
        if( isReady )
        {
            //Is the calc point itself disabled?
            CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_pointId);
            if( ! componentPointPtr || componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT) )
            {
                isReady = false;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return isReady;
}

/*******************************
*
*   Takes the interval requested in seconds
*   and calculates when the top of the next interval
*   would be
*   ie.  60 seconds interval should be updating
*       once a minute at the top of the minute
********************************
*/
void CtiCalc::setNextInterval( int aInterval )
{
    CtiTime timeNow;
    if(aInterval > 0)
    {
        _nextInterval = nextScheduledTimeAlignedOnRate(timeNow, aInterval).seconds();
    }
    else
    {
        _nextInterval = timeNow.seconds();
    }
}


ULONG CtiCalc::getNextInterval( ) const
{
    return _nextInterval;
}

int CtiCalc::getUpdateInterval( ) const
{
    return _updateInterval;
}

long CtiCalc::getRegressionComponentId() const
{
    return _regressionPtId;
}

long CtiCalc::findDemandAvgComponentPointId()
{
    long returnPointId = 0;
    //  Iterate through all of the calculations in the collection
    for( auto& tmpComponent : _components )
    {
        // 20050202 CGP // If the push operator was used, the point we choose will be the LAST one on the stack!
        if(tmpComponent->getComponentPointId() > 0)
        {
            returnPointId = tmpComponent->getComponentPointId();
        }

        const string& functionName = tmpComponent->getFunctionName();
        if( findStringIgnoreCase(functionName, "DemandAvg") )
        {
            if(tmpComponent->getComponentPointId() > 0)
                returnPointId = tmpComponent->getComponentPointId();    // else, it is the last point id found!
            break;
        }
    }

    return returnPointId;
}

CtiTime CtiCalc::calcTimeFromComponentTime( const CtiTime &minTime, const CtiTime &maxTime )
{
    CtiTime rtime;

    if(getUpdateType() != CalcUpdateType::Periodic)
    {
        rtime = maxTime;
    }

    return rtime;
}

/*
 *  Determines if all valid calc components have the same timestamp.  Nonupdated and Constant Qualities do not affect the timestamp output.
 */
bool CtiCalc::calcTimeFromComponentTime( CtiTime &componentTime, int componentQuality, CtiTime &minTime, CtiTime &maxTime )
{
    if( componentQuality != NonUpdatedQuality &&        // Timestamps are ignored on non-updated or constant quality points.
        componentQuality != ConstantQuality )
    {
        if(minTime > componentTime)
            minTime = componentTime;

        if(maxTime < componentTime)
            maxTime = componentTime;
    }

    return minTime == maxTime;
}

int CtiCalc::calcQualityFromComponentQuality( int qualityFlag, const CtiTime &minTime, const CtiTime &maxTime )
{
    int component_quality = NormalQuality;

    if( _calculateQuality )
    {
        if(qualityFlag & (1 << ManualQuality) )
        {
            component_quality = ManualQuality;
            qualityFlag &= ~(1 << ManualQuality);
        }

        /* 20060210 CGP - A constant component does not imply a constant result.
        if(qualityFlag & (1 << ConstantQuality) )
        {
            component_quality = ConstantQuality;
            qualityFlag &= ~(1 << ConstantQuality);
        }
        */

        if(qualityFlag & (1 << NonUpdatedQuality) )
        {
            component_quality = NonUpdatedQuality;
        }
        else if(qualityFlag & ~(1 << NormalQuality))    // There is a bit set other than Normal or NonUpdated.
        {
            component_quality = QuestionableQuality;
        }

        if(getUpdateType() == CalcUpdateType::PeriodicPlusUpdate)
        {
            if(component_quality == NormalQuality && maxTime.seconds() - minTime.seconds() > getUpdateInterval())
            {
                component_quality = QuestionableQuality;
            }
        }
    }

    return component_quality;
}



double CtiCalc::figureDemandAvg(long secondsInAvg)
{
    double retVal = 0.0;

    try
    {
        CtiTime currenttime;
        // Use the value of TOS.  This will be the pointvalue, but should be the result of any preceeding calculations.
        double componentPointValue = pop();
        long componentId = findDemandAvgComponentPointId();
        long updatesInCurrentAvg = 0;

        if(componentId > 0)
        {
            CtiPointStoreElement* componentPointPtr = CtiPointStore::find(componentId);
            CtiPointStoreElement* calcPointPtr      = CtiPointStore::find(getPointId());

            if( calcPointPtr )
            {
                updatesInCurrentAvg = calcPointPtr->getUpdatesInCurrentAvg();

                // CGP 20050302 We use this count as an indicator that we are beginning a new interval.
                if( updatesInCurrentAvg == 0 )
                {
                    calcPointPtr->setPointCalcWindowEndTime(nextScheduledTimeAlignedOnRate(currenttime, secondsInAvg));
                }
            }

            if(componentPointPtr && calcPointPtr)
            {
                /*
                 *  This is an odd little decision.  Apparently we are using the time between the last two component point
                 *   changes (componentPointPtr->getSecondsSincePreviousPointTime()) as the "slop" value defining the interval ending time.
                 *
                 *   if resolves to if pointdata->time is greater than the windowBEGIN + slop and pointdata->time < windowEND + slop
                 *
                 *   SLOP is very very SLOPPY!  If the data is not coming in on a schedule, the results are questionable.
                 *   If the results are coming in at the rate of the DA, this will compute every other interval.
                 */
                if( componentPointPtr->getPointTime().seconds() >= (calcPointPtr->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime() - secondsInAvg) &&
                    componentPointPtr->getPointTime().seconds() < (calcPointPtr->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime()) )
                {//is the last point data received in the average or not
                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                    {
                        Cti::FormattedList loglist;
                        loglist.add("Current Component Point Time")       << componentPointPtr->getPointTime();
                        loglist.add("Current Point Calc Window End Time") << calcPointPtr->getPointCalcWindowEndTime();

                        CTILOG_DEBUG(dout, loglist);
                    }
                    double currentCalcPointValue = calcPointPtr->getPointValue();

                    double currentTotal = currentCalcPointValue * updatesInCurrentAvg;
                    updatesInCurrentAvg++;
                    retVal = (currentTotal + componentPointValue) / updatesInCurrentAvg;

                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                    {
                        Cti::FormattedList loglist;
                        loglist.add("Current Calc Point Value")  << currentCalcPointValue;
                        //loglist.add("Current Total")             << currentTotal;
                        loglist.add("Updates In Current Avg")    << updatesInCurrentAvg;
                        loglist.add("Component Point Value")     << componentPointValue;
                        loglist.add("New Calc Point Value")      << retVal;
                        loglist.add("Will Send point change at") << calcPointPtr->getPointCalcWindowEndTime();

                        CTILOG_DEBUG(dout, loglist);
                    }
                }
                else
                {
                    if( updatesInCurrentAvg > 0 )
                    {
                        retVal = componentPointValue;
                        updatesInCurrentAvg = 1;
                        if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                        {
                            Cti::FormattedList loglist;
                            loglist <<"NEW DEMAND AVERAGE BEGUN";
                            //loglist.add("Current Component Point Time: "              << componentPointPtr->getPointTime().asString();
                            loglist.add("Current Point Calc Window End Time")         << calcPointPtr->getPointCalcWindowEndTime();
                            //loglist.add("Seconds Since Previous Point Time")          << componentPointPtr->getSecondsSincePreviousPointTime();
                            loglist.add("New Initial Demand Avg")                     << retVal;
                            loglist.add("Updates In Current Avg")                     << updatesInCurrentAvg;
                            loglist.add("Previous demand average has a timestamp of") << calcPointPtr->getPointCalcWindowEndTime();
                            loglist.add("Next demand average will have timestamp of") << CtiTime(calcPointPtr->getPointCalcWindowEndTime().seconds()+secondsInAvg);

                            CTILOG_DEBUG(dout, loglist);
                        }
                    }
                    else
                    {
                        retVal = componentPointValue;
                        updatesInCurrentAvg = 1;
                        if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                        {
                            CTILOG_DEBUG(dout, "Calc Point Id: "<< getPointId() <<" Demand Avg Reset!");
                        }
                    }
                    calcPointPtr->setPointCalcWindowEndTime(nextScheduledTimeAlignedOnRate(currenttime, secondsInAvg));
                }

                calcPointPtr->setUpdatesInCurrentAvg(updatesInCurrentAvg);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed point: "<< getPointId());
    }

    return retVal;
}

int CtiCalc::getComponentCount()
{
    return getComponentIDList().size();
}

set<long> CtiCalc::getComponentIDList() const
{
    return boost::copy_range<set<long>>(
        _components
            | boost::adaptors::indirected
            | boost::adaptors::transformed(std::mem_fn(&CtiCalcComponent::getComponentPointId))
            | boost::adaptors::filtered([](int id) { return id > 0; }));
}
