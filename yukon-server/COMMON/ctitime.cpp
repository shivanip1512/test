#include "yukon.h"

#include <time.h>
#include <boost/thread/mutex.hpp>
#include <boost/thread/tss.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>
#include <utility>
#include <rw/rwtime.h>

#include "ctidate.h"
#include "ctitime.h"

using std::string;

const int SECONDS_PER_MINUTE = 60;
const int MINUTES_PER_HOUR = 60;

const int HOURS_PER_DAY = 24;

typedef boost::date_time::us_dst_rules<boost::gregorian::date, boost::posix_time::time_duration> us_dst_rules;

struct thread_tm
{
    enum type {local, gm};
    struct tm* ctm;

    thread_tm(type t, const time_t* tt)
    {
        ctm = new struct tm;
        struct tm* _ctm;
        if(t == local){
            _ctm = ::localtime(tt);
        } else {
            _ctm = ::gmtime(tt);
        }
        ::memcpy(ctm, _ctm, sizeof(struct tm));
    }

    ~thread_tm()
    {
        delete ctm;
    }
};

boost::thread_specific_ptr<struct thread_tm> tm_value;

ctitime_t CtiTime::maketm(const CtiDate& d, unsigned hh, unsigned mm, unsigned ss){
    tm ctm;
    ctm.tm_sec = ss;     /* seconds after the minute - [0,59] */
    ctm.tm_min = mm;     /* minutes after the hour - [0,59] */
    ctm.tm_hour = hh;    /* hours since midnight - [0,23] */
    ctm.tm_mday = d.dayOfMonth();    /* day of the month - [1,31] */
    ctm.tm_mon = d.month() - 1;     /* months since January - [0,11] */
    ctm.tm_year = d.year() - 1900;    /* years since 1900 */
    ctm.tm_isdst = -1;   /* daylight savings time flag */
    return mktime(&ctm);
}

CtiTime::CtiTime() :
  _seconds(time(NULL))
{}

CtiTime::CtiTime(specialvalues sv) :
  _seconds(0)
{
    if(sv == CtiTime::neg_infin)
    {
        _seconds = std::numeric_limits<ctitime_t>::min();
    }
    else if(sv == CtiTime::pos_infin)
    {
        _seconds = std::numeric_limits<ctitime_t>::max();
    }
}


CtiTime::CtiTime(unsigned hh, unsigned mm, unsigned ss) :
  _seconds(maketm(CtiDate::now(), hh, mm, ss))
{}

CtiTime::CtiTime(const CtiDate& d, unsigned hh, unsigned mm, unsigned ss) :
  _seconds(0)
{
    if(d.isValid()) {
        if(d.is_neg_infinity() || d.isStartOfTime()){
            _seconds = std::numeric_limits<ctitime_t>::min();
        } else if(d.is_pos_infinity() || d.isEndOfTime()){
            _seconds = std::numeric_limits<ctitime_t>::max();
        } else {
            _seconds = maketm(d, hh, mm, ss);
        }
    }
}


/*
* CAUTION takes an unsigned long, but it casts it as an int.
*/
CtiTime::CtiTime(unsigned long s)
{
    //assert( s < YUKONEOT);
    if ((int)s == -1)
        _seconds = 0;
    else
        _seconds = s;
}

CtiTime::CtiTime(struct tm* ctm) :
  _seconds(0)
{
    time_t tt = mktime(ctm);
    if (tt > 0) {
        _seconds = tt;
    }
}


CtiTime::CtiTime(const CtiTime& ct) :
  _seconds(ct._seconds)
{}


CtiTime& CtiTime::operator = (const CtiTime& ct)
{
    _seconds = ct._seconds;
    return *this;
}

CtiTime& CtiTime::addSeconds(const int secs)
{
    if( !is_special() ){
        //boost::mutex::scoped_lock scoped_lock(_secs_mutex);
        _seconds += secs;
    }
    return *this;
}


CtiTime& CtiTime::addMinutes(const int mins)
{
    return addSeconds(mins*SECONDS_PER_MINUTE );
}

/*
 *  Will add one day to the time. flag is set to true so that if you add a day from mignight
 *  and it crosses a DST boundary, the result is still midnight the following day.
 *  Set flag = false if you do not wish to keep the time the same while adding days.
 */
CtiTime CtiTime::addDays(const int days, bool DSTflag)
{
    if (DSTflag) {

        CtiTime DSTtest = *this;
        DSTtest.addSeconds(days*HOURS_PER_DAY*SECONDS_PER_MINUTE *MINUTES_PER_HOUR);

        if ( DSTtest.isDST() == isDST() )
            return addSeconds(days*HOURS_PER_DAY*SECONDS_PER_MINUTE *MINUTES_PER_HOUR);
        if ( isDST() ) {
            return addSeconds( days*HOURS_PER_DAY*SECONDS_PER_MINUTE *MINUTES_PER_HOUR + SECONDS_PER_MINUTE *MINUTES_PER_HOUR );
        }else
            return addSeconds( days*HOURS_PER_DAY*SECONDS_PER_MINUTE *MINUTES_PER_HOUR - SECONDS_PER_MINUTE *MINUTES_PER_HOUR );

    }else{
        return addSeconds(days*HOURS_PER_DAY*SECONDS_PER_MINUTE *MINUTES_PER_HOUR);
    }

}


CtiTime& CtiTime::operator += (const int secs)
{
    return addSeconds(secs);
}

CtiTime& CtiTime::operator -= (const int secs)
{
    return addSeconds(-1*secs);
}

CtiDate CtiTime::date() const
{
    CtiDate cd(CtiDate::not_a_date);
    if(!is_special()){
        struct tm ctm;
        ctm = *localtime(&_seconds);
        cd = CtiDate( ctm.tm_mday, ctm.tm_mon+1, ctm.tm_year+1900);
        //cd = CtiDate( this);
    } else if(is_neg_infinity()) {
        cd = CtiDate(CtiDate::neg_infin);
    } else if(is_pos_infinity()) {
        cd = CtiDate(CtiDate::pos_infin);
    }
    return cd;
}

CtiDate CtiTime::dateGMT() const
{
    CtiDate cd(CtiDate::not_a_date);
    if(!is_special()){
        struct tm ctm;
        ctm = *gmtime(&_seconds);
        cd = CtiDate( ctm.tm_mday, ctm.tm_mon+1, ctm.tm_year+1900);
        //cd = CtiDate( this);
    } else if(is_neg_infinity()) {
        cd = CtiDate(CtiDate::neg_infin);
    } else if(is_pos_infinity()) {
        cd = CtiDate(CtiDate::pos_infin);
    }
    return cd;
}

int CtiTime::day() const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *localtime(&_seconds);
        return ctm.tm_mday;
    }
    return -1;
}

int CtiTime::dayGMT() const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *gmtime(&_seconds);
        return ctm.tm_mday;
    }
    return -1;
}

int CtiTime::second()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *localtime(&_seconds);
        return ctm.tm_sec;
    }
    return -1;
}

int CtiTime::secondGMT()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *gmtime(&_seconds);
        return ctm.tm_sec;
    }
    return -1;
}

int CtiTime::minute()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *localtime(&_seconds);
        return ctm.tm_min;
    }
    return -1;
}

int CtiTime::minuteGMT()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *gmtime(&_seconds);
        return ctm.tm_min;
    }
    return -1;
}

int CtiTime::hour()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *localtime(&_seconds);
        return ctm.tm_hour;
    }
    return -1;
}

int CtiTime::hourGMT()  const
{
    if(!is_special()){
        struct tm ctm;
        ctm = *gmtime(&_seconds);
        return ctm.tm_hour;
    }
    return -1;
}

ctitime_t CtiTime::seconds() const
{
    return _seconds;
}

/**
 * Creates a new CtiTime by interpreting local_seconds as
 * wall-clock time in the server's timezone.<br>
 *
 * This includes any DST adjustment.<br>
 *
 * local_seconds is defined as:<br>
 *
 * local_seconds = gmt_seconds + local_offset + dst_offset<br>
 *
 * Multiple gmt_seconds values can result in identical local_seconds values near the DST shift.
 * <pre>
 * 4AECDD90 (11/1/2009 01:00:00) = 4AED23E0 (11/1/2009 06:00) + (3600 * -6) + (3600)
 * 4AECDD90 (11/1/2009 01:00:00) = 4AED31F0 (11/1/2009 07:00) + (3600 * -6) + (0)
 * </pre>
 *
 * @param local_seconds
 *               Seconds since 01/01/1970 00:00, wall-clock time
 *
 * @return CtiTime
 */
CtiTime CtiTime::fromLocalSeconds(const unsigned long local_seconds)
{
    _TIME_ZONE_INFORMATION tzinfo;
    GetTimeZoneInformation(&tzinfo);

    //  attempt a rough estimate of GMT time from local time by using the standard time offset
    ctitime_t gmt_seconds = local_seconds + (tzinfo.Bias + tzinfo.StandardBias) * SECONDS_PER_MINUTE;

    //  need to determine if we were in DST or not
    tm ctm = *localtime(&gmt_seconds);

    int minutes_offset = tzinfo.Bias + (ctm.tm_isdst ? tzinfo.DaylightBias : tzinfo.StandardBias);

    return CtiTime(local_seconds + minutes_offset * SECONDS_PER_MINUTE);
}

bool CtiTime::isValid() const
{
    return _seconds > 0 && _seconds != std::numeric_limits<ctitime_t>::max();
}

bool CtiTime::is_special() const
{
    return (_seconds == 0) || (is_pos_infinity()) || (is_neg_infinity());
}


bool CtiTime::is_neg_infinity() const
{
    return _seconds == std::numeric_limits<ctitime_t>::min();
}

bool CtiTime::is_pos_infinity() const
{
    return _seconds == std::numeric_limits<ctitime_t>::max();
}

bool CtiTime::isDST() const
{
    struct tm ctm;
    ctm = *localtime(&_seconds);
    return ctm.tm_isdst;
}

// tm is for local
void CtiTime::extract(struct tm* ctm) const
{
    struct tm *extracted;

    if( isValid() && (extracted = localtime(&_seconds)) )
    {
        *ctm = *extracted;
    }
    else
    {
        ctm->tm_hour  = 0;
        ctm->tm_isdst = 0;
        ctm->tm_mday  = 0;
        ctm->tm_min   = 0;
        ctm->tm_mon   = 0;
        ctm->tm_sec   = 0;
        ctm->tm_wday  = 0;
        ctm->tm_yday  = 0;
        ctm->tm_year  = 0;
    }
}

unsigned long CtiTime::toRwSeconds() const
{
    return _seconds + rwEpoch;
}


string CtiTime::asString()  const
{
    if(!isValid()){
        return string("not-a-time");
    } else if(is_neg_infinity()){
        return string("neg-infinity");
    } else if(is_pos_infinity()){
        return string("pos-infinity");
    }

    //  date format "mm/dd/yyyy HH:MM:SS" - 19 chars needed, plus null
    char time_str[20];

    strftime(time_str, 20, "%m/%d/%Y %H:%M:%S", localtime(&_seconds));
    time_str[19] = 0;

#if 0
    std::stringstream ss;
    ss.imbue(std::locale::classic());
    ss << s;
    return ss.str();
#else
    return string(time_str);
#endif
}

string CtiTime::asGMTString()  const
{
    if(!isValid()){
        return string("not-a-time");
    } else if(is_neg_infinity()){
        return string("neg-infinity");
    } else if(is_pos_infinity()){
        return string("pos-infinity");
    }

    //  date format "mm/dd/yyyy HH:MM:SS" - 19 chars needed, plus null
    char time_str[20];

    strftime(time_str, 20, "%m/%d/%Y %H:%M:%S", gmtime(&_seconds));
    time_str[19] = 0;

#if 0
    std::stringstream ss;
    ss.imbue(std::locale::classic());
    ss << s;
    return ss.str();
#else
    return string(time_str);
#endif
}

struct tm* CtiTime::gmtime_r(const time_t *tod){
    struct thread_tm* ss = new thread_tm(thread_tm::gm, tod);
    tm_value.reset(ss);
    tm_value->ctm->tm_isdst = CtiTime().isDST();
    return tm_value->ctm;
}


struct tm* CtiTime::localtime_r(const time_t *tod){
    struct thread_tm* ss = new thread_tm(thread_tm::local, tod);
    tm_value.reset(ss);
    return tm_value->ctm;
}


CtiTime CtiTime::now()
{
    return CtiTime();
}

void CtiTime::resetToNow()
{
    CtiTime temp = CtiTime();
    _seconds = temp.seconds();
}

CtiTime CtiTime::beginDST(unsigned year)
{
    boost::gregorian::date d = us_dst_rules::local_dst_start_day(year);
    boost::posix_time::time_duration td = us_dst_rules::dst_offset();
    return CtiTime(CtiDate(d.day(), d.month(), d.year()), td.hours() + 2, td.minutes(), td.seconds());
}
CtiTime CtiTime::endDST(unsigned int year)
{
    boost::gregorian::date d = us_dst_rules::local_dst_end_day(year);
    boost::posix_time::time_duration td = us_dst_rules::dst_offset();
    return CtiTime(CtiDate(d.day(), d.month(), d.year()), td.hours(), td.minutes(), td.seconds()) - SECONDS_PER_MINUTE*MINUTES_PER_HOUR;
}


long CtiTime::findTZ()
{
    char *tz;
    tz = getenv( "TZ" );
    return timezone;
}


bool operator < (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds < d2._seconds;
}
bool operator <= (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds <= d2._seconds;
}
bool operator > (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds > d2._seconds;
}
bool operator >= (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds >= d2._seconds;
}
bool operator == (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds == d2._seconds;
}
bool operator != (const CtiTime& d1, const CtiTime& d2)
{
    return d1._seconds != d2._seconds;
}

CtiTime operator + (const CtiTime& t, const unsigned long s)
{
    CtiTime _t(t);
    _t.addSeconds(s);
    return _t;
}

CtiTime operator - (const CtiTime& t, const unsigned long s)
{
    CtiTime _t(t);
    _t.addSeconds(-1*s);
    return _t;
}
std::ostream& operator<< (std::ostream& s, const CtiTime& t)
{
    s << t.asString();
    return s;
}

/*
    Returns the number of seconds of offset between the local time and the same time expressed in GMT.
        Q: How many seconds between (for example) 01/01/2010 00:00:00 GMT and 01/01/2010 00:00:00 CST
        A:  21600
        Q: How many seconds between (for example) 07/01/2010 00:00:00 GMT and 07/01/2010 00:00:00 CDT
        A:  18000
*/
long CtiTime::secondOffsetToGMT() const
{
    tm ctm = *localtime(&_seconds);

    _TIME_ZONE_INFORMATION tzinfo;
    GetTimeZoneInformation(&tzinfo);

    return (tzinfo.Bias + (ctm.tm_isdst ? tzinfo.DaylightBias : tzinfo.StandardBias)) * SECONDS_PER_MINUTE;
}

