#ifndef __BOOST_TIME_H__
#define __BOOST_TIME_H__

#include "boost/date_time/posix_time/posix_time.hpp"
#include "boost/date_time/gregorian/greg_date.hpp"

using namespace boost::posix_time;

//  perhaps we'll bring in the whole namespace someday, but right now, all we need is date
using boost::gregorian::date;

inline unsigned long ptime_to_utc_seconds(const ptime &p)
{
    ptime::time_duration_type t = p - ptime(date(1970, 1, 1));

    return t.total_seconds();
}

inline ptime::time_duration_type ptime_to_utc_duration(const ptime &p)
{
    ptime::time_duration_type t = p - ptime(date(1970, 1, 1));

    return t;
}

#endif // __BOOST_TIME_H__
