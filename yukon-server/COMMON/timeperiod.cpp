/***********************************************************  *
* TimePeriod class encapsulates a start and end time. Useful for
* a single object to hold a known span of time and functionality
* to work with spans of time
* */
#include "precompiled.h"
#include <time.h>

#include "ctitime.h"
#include <boost/date_time/posix_time/posix_time.hpp>
#include "timeperiod.h"

using boost::posix_time::ptime;
using boost::posix_time::time_period;

namespace Cti {

TimePeriod::TimePeriod(const CtiTime &timeBegin, const CtiTime &timeEnd) :
_period(boost::posix_time::from_time_t(timeBegin.seconds()), boost::posix_time::from_time_t(timeEnd.seconds()))
{
    if( timeBegin.is_special() || timeEnd.is_special() )
    {
        ptime startTime = _period.begin();
        ptime endTime = _period.end();
        if( timeBegin.is_neg_infinity() )
        {
            startTime = ptime::ptime(boost::date_time::neg_infin);
        }
        else if( timeBegin.is_pos_infinity() )
        {
            startTime = ptime::ptime(boost::date_time::pos_infin);
        }

        if( timeEnd.is_neg_infinity() )
        {
            endTime = ptime::ptime(boost::date_time::neg_infin);
        }
        else if( timeEnd.is_pos_infinity() )
        {
            endTime = ptime::ptime(boost::date_time::pos_infin);
        }

        _period = time_period(startTime, endTime);
    }
}

TimePeriod::TimePeriod(const TimePeriod &rhs) :
_period(rhs._period)
{
}

TimePeriod::TimePeriod(const time_period &rhs) :
_period(rhs)
{
}

TimePeriod& TimePeriod::operator = (const TimePeriod& rhs)
{
    _period = rhs._period;
    return *this;
}

TimePeriod& TimePeriod::addDays(const int days, bool DSTflag)
{
    if( !is_null() )
    {
        // We trust and know how CtiTime add Days work, so we are hijacking this function
        // ptime add Days does not allow for dst flag checking.
        CtiTime start = begin();
        CtiTime finish = end();

        start.addDays(days, DSTflag);
        finish.addDays(days, DSTflag);
        _period = time_period(boost::posix_time::from_time_t(start.seconds()), boost::posix_time::from_time_t(finish.seconds()));
    }

    return *this;
}

bool TimePeriod::is_null()
{
    return _period.is_null();
}

bool TimePeriod::contains(CtiTime &time)
{
    ptime theTime(boost::posix_time::from_time_t(time.seconds()));
    return _period.contains(theTime);
}

bool TimePeriod::intersects(const TimePeriod &rhs)
{
    return _period.intersects(rhs._period);
}

TimePeriod TimePeriod::intersection(const TimePeriod &rhs)
{
    return TimePeriod(_period.intersection(rhs._period));
}

TimePeriod TimePeriod::merge(const TimePeriod &rhs)
{
    return TimePeriod(_period.merge(rhs._period));
}

CtiTime TimePeriod::begin()
{
    if( _period.begin().is_pos_infinity() )
    {
        return CtiTime::CtiTime(CtiTime::pos_infin);
    }
    else if( _period.begin().is_neg_infinity() )
    {
        return CtiTime::CtiTime(CtiTime::neg_infin);
    }
    return CtiTime::CtiTime(_mkgmtime(&to_tm(_period.begin())));
}

CtiTime TimePeriod::end()
{
    if( _period.end().is_pos_infinity() )
    {
        return CtiTime::CtiTime(CtiTime::pos_infin);
    }
    else if( _period.end().is_neg_infinity() )
    {
        return CtiTime::CtiTime(CtiTime::neg_infin);
    }
    return CtiTime::CtiTime(_mkgmtime(&to_tm(_period.end())));
}

}// namespace CTI
