#pragma once

/***********************************************************  * 
* TimePeriod class encapsulates a start and end time. Useful for
* a single object to hold a known span of time and functionality
* to work with spans of time
* */

class CtiTime;
#include "dlldefs.h"
#include <boost/date_time/posix_time/posix_time.hpp>

namespace Cti {

class IM_EX_CTIBASE TimePeriod
{
private:
    boost::posix_time::time_period _period;
    TimePeriod(const boost::posix_time::time_period &rhs);

public:
    // Construct TimePeriod with begin and end time. Note that end is not part of the period.
    // Creates [timeBegin, timeEnd)
    TimePeriod(const CtiTime &timeBegin, const CtiTime &timeEnd);
    TimePeriod(const TimePeriod &rhs);

    TimePeriod& operator = (const TimePeriod&);

    // Add days to both time begin and end. Compensates for DST by default.
    TimePeriod& addDays(const int days, bool DSTflag = true);

    // True if period is not well formed. eg: end is less than or equal to begin.
    bool is_null();

    // returns true if time is inside the period.
    bool contains(CtiTime &time);

    // returns true if the periods intersect
    bool intersects(const TimePeriod &rhs);

    // Returns a new time period that is the intersection of two periods
    // isValid = false if no intersection.
    TimePeriod intersection(const TimePeriod &rhs);

    // Returns union of two periods. isValid = false if no intersection.
    TimePeriod merge(const TimePeriod &rhs);

    // returns first time in the period
    CtiTime begin();
    // returns the instant past the last time in the period.
    // E.X. TimePeriod(Time, April 1 2009).end() = april 1 2009.
    // april 1 2009 is not considered part of the time period.
    CtiTime end();
};

}// namespace CTI
