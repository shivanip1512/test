
#include "yukon.h"

#include <cstdlib>
#include <sstream>
#include <utility>

#include "TimeOfDayStrategy.h"
#include "utility.h"


TimeOfDayStrategy::TimeOfDayStrategy()
    : ControlStrategy()
{
    // empty!
}


TimeOfDayStrategy::~TimeOfDayStrategy()
{
    // empty!
}


const long TimeOfDayStrategy::getTimeOffset(const std::string &inputTime) const
{
    int     hour    = -1;
    int     minute  = -1;
    char    colon   = 'X';

    std::istringstream  timeStream(inputTime);

    timeStream >> hour >> colon >> minute;

    if ( colon != ':' || hour < 0 || hour > 23 || minute < 0 || minute > 59 )   // "00:00" --> "23:59"
    {
        return -1;
    }

    return ( 60 * ( minute + ( 60 * hour )));
}


void TimeOfDayStrategy::restoreParameters( const std::string &name, const std::string &type, const std::string &value )
{
    double  newValue    = std::atof( value.c_str() );
    long    timeOffset  = getTimeOffset( name );

    if ( timeOffset != -1 )
    {
        TimeOfDayMapping::iterator  iter = _timeOfDay.find( timeOffset );

        if ( iter != _timeOfDay.end() )
        {
            TimeOfDay & tod = iter->second;

            if (ciStringEqual(type, "WeekDay"))
            {
                tod.weekdayPercent = newValue;
            }
            else
            {
                tod.weekendPercent = newValue;
            }
        }
        else
        {
            TimeOfDay   tod;
            
            tod.timeOffset = timeOffset;

            if (ciStringEqual(type, "WeekDay"))
            {
                tod.weekdayPercent = newValue;
                tod.weekendPercent = 0.0;
            }
            else
            {
                tod.weekdayPercent = 0.0;
                tod.weekendPercent = newValue;
            }

            _timeOfDay.insert( std::make_pair(tod.timeOffset, tod) );

        }
    }
}


TimeOfDayStrategy::TimeOfDay TimeOfDayStrategy::getNextTimeOfDay( const long timeOffset ) const
{
    TimeOfDayMapping::const_iterator  iter = _timeOfDay.upper_bound( timeOffset );

    if ( iter == _timeOfDay.end() )     // not found - get the first one
    {
        iter = _timeOfDay.begin();
    }

    return iter->second;
}


const ControlStrategy::ControlMethodType TimeOfDayStrategy::getMethodType() const
{
    return ControlStrategy::TimeOfDayMethod;
}


const std::string TimeOfDayStrategy::getControlMethod() const
{
    return ControlStrategy::TimeOfDayControlMethod;
}


void TimeOfDayStrategy::setControlMethod(const std::string & method) /* empty */
{
    // empty!
}


const ControlStrategy::ControlUnitType TimeOfDayStrategy::getUnitType() const
{
    return ControlStrategy::TimeOfDayUnit;
}


const std::string TimeOfDayStrategy::getControlUnits() const
{
    return ControlStrategy::TimeOfDayControlUnit;
}


void TimeOfDayStrategy::execute()
{
    // empty for now....
}

