#include "yukon.h"
#include "rwutil.h"

date to_boost_date(const RWDate& rw_date)
{
    return date(rw_date.year(), rw_date.month(), rw_date.dayOfMonth());
}

ptime to_boost_ptime(const RWTime& rw_time)
{
    struct tm tm;
    rw_time.extract(&tm);
    time_t tt = mktime(&tm);
    return boost::posix_time::from_time_t(tt);
}

ptime to_boost_ptime(const RWDBDateTime& rwdb_datetime)
{
    struct tm tm;
    rwdb_datetime.extract(&tm);
    time_t tt = mktime(&tm);
    return boost::posix_time::from_time_t(tt);
}
/*
RWSet makeRWSet(const set<T>& std_set)
{
    RWSet rw_set;
    for(set<T>::iterator iter = std_set.begin(); iter != std_set.end(); iter++)
    {
    rw_set.insert(*iter);
    }
    return rw_set;
}*/

RWvostream& operator<<(RWvostream& strm, const string& s)
{
    return strm << RWCString((const char*) s.data());
}

RWvistream&  operator>>(RWvistream& strm, string& s)
{
    RWCString rw_str;
    strm >> rw_str;
    s = (const char*) rw_str.data();
    return strm;
}

RWDBReader& operator>>(RWDBReader& rdr, string& s)
{
    RWCString rw_str;
    rdr >> rw_str;
    s = (const char*) rw_str.data();
    return rdr ;
}

RWDBReader&   operator>>(RWDBReader&   rdr, ptime& p)
{
    RWTime t;
    rdr >> t;
    p = to_boost_ptime(t);
    return rdr;
}

RWDBInserter& operator<<(RWDBInserter& ins, const string &s)
{
    return ins << RWCString(s.data());
}

RWDBInserter& operator<<(RWDBInserter& ins, const ptime& p)
{
    RWDBDateTime dbdt(RWTime(ptime_to_utc_seconds(p) + rwEpoch));
    return ins << dbdt;
}

