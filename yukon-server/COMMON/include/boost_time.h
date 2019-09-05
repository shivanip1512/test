#pragma once

#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/date_time/gregorian/greg_date.hpp>

inline unsigned long ptime_to_utc_seconds(const boost::posix_time::ptime &p)
{
    boost::posix_time::ptime::time_duration_type t = p - boost::posix_time::ptime(boost::gregorian::date(1970, 1, 1));

    return t.total_seconds();
}

inline boost::posix_time::ptime::time_duration_type ptime_to_utc_duration(const boost::posix_time::ptime &p)
{
    boost::posix_time::ptime::time_duration_type t = p - boost::posix_time::ptime(boost::gregorian::date(1970, 1, 1));

    return t;
}
