#ifndef __BOOST_TIME_H__
#define __BOOST_TIME_H__

#include "boost/date_time/posix_time/posix_time.hpp"
#include "boost/date_time/gregorian/greg_date.hpp"

//  it may be prudent to bring the entire gregorian namespace in,
//    and possibly the entire posix_time namespace...  but for now,
//    these are really the only classes we need
using boost::posix_time::ptime;
using boost::gregorian::date;

using boost::posix_time::second_clock;
using boost::posix_time::hours;
using boost::posix_time::minutes;
using boost::posix_time::seconds;


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
