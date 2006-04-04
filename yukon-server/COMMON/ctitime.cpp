#include "yukon.h"
                                         
#include <time.h>
#include <boost/thread/mutex.hpp>
#include <boost/thread/tss.hpp>
#include <boost/date_time/posix_time/posix_time_types.hpp>
#include <utility>

#include "ctidate.h"
#include "ctitime.h"



typedef boost::date_time::us_dst_rules<boost::gregorian::date, boost::posix_time::time_duration> us_dst_rules;
boost::mutex ctime_mutex;

struct thread_tm
{
    enum type {local, gm};
    struct tm* ctm;

    thread_tm(type t, const time_t* tt)
    {
        ctm = new struct tm;
        boost::mutex::scoped_lock lock(ctime_mutex);
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

ctitime_t CtiTime::maketm(CtiDate& d, unsigned hour, unsigned minute, unsigned second){
    tm ctm;
    ctm.tm_sec = second;     /* seconds after the minute - [0,59] */
    ctm.tm_min = minute;     /* minutes after the hour - [0,59] */
    ctm.tm_hour = hour;    /* hours since midnight - [0,23] */
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


CtiTime::CtiTime(unsigned hour, unsigned minute, unsigned second) :
  _seconds(maketm(CtiDate::now(), hour, minute, second))
{}

CtiTime::CtiTime(CtiDate& d, unsigned hour, unsigned minute, unsigned second) :
  _seconds(0)
{
    if(d.isValid()) {
        if(d.is_neg_infinity()){
            _seconds = std::numeric_limits<ctitime_t>::min();
        } else if(d.is_pos_infinity()){
            _seconds = std::numeric_limits<ctitime_t>::max();
        } else {
            _seconds = maketm(d, hour, minute, second);
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
    return addSeconds(mins*60);
}

CtiTime CtiTime::addDays(const int days)
{
    CtiTime DSTtest = *this;
    DSTtest.addSeconds(days*24*60*60);

    if ( DSTtest.isDST() == isDST() )
        return addSeconds(days*24*60*60);
    if ( isDST() ) {
        // Opposite of what it should be: This is only happening in the addDay
        // because of Jesse. blame canada!
        return addSeconds( days*24*60*60 + 3600 );
    }else
        return addSeconds( days*24*60*60 - 3600 );
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
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
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
int CtiTime::day() const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *localtime(&_seconds);
        return ctm.tm_mday;
    }
    return -1;


}
int CtiTime::second()  const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *localtime(&_seconds);
        return ctm.tm_sec;
    }
    return -1;
}


int CtiTime::minute()  const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *localtime(&_seconds);
        return ctm.tm_min;
    }
    return -1;
}
int CtiTime::minuteGMT()  const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *gmtime(&_seconds);
        return ctm.tm_min;
    }
    return -1;
}


int CtiTime::hour()  const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *localtime(&_seconds);
        return ctm.tm_hour;
    }
    return -1;
}

int CtiTime::hourGMT()  const
{
    if(!is_special()){
        struct tm ctm;
        boost::mutex::scoped_lock scoped_lock(ctime_mutex);
        ctm = *gmtime(&_seconds);
        return ctm.tm_hour;
    }
    return -1;


}

ctitime_t CtiTime::seconds() const
{
    return _seconds;
}

/*
 * BE careful when you use this method!
 * this will change the seconds of this time
 */

CtiTime& CtiTime::toUTCtime()
{
    getenv( "TZ" );
    addSeconds(-1*timezone);
    return *this;
}

bool CtiTime::isValid() const
{
    return _seconds != 0;
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
    boost::mutex::scoped_lock scoped_lock(ctime_mutex);
    ctm = *localtime(&_seconds);
    return ctm.tm_isdst;
}

// tm is for local
void CtiTime::extract(struct tm* ctm) const
{   
    boost::mutex::scoped_lock scoped_lock(ctime_mutex);
    *ctm = *localtime(&_seconds);                     
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
    boost::mutex::scoped_lock scoped_lock(ctime_mutex);
    string s(ctime(&_seconds));
    s = s.substr(0, s.length() - 1);
    std::stringstream ss;
    ss.imbue(std::locale::classic()); 
    ss << s;
    return ss.str(); 
}

struct tm* CtiTime::gmtime_r(const time_t *tod){
    struct thread_tm* ss = new thread_tm(thread_tm::gm, tod);
    tm_value.reset(ss);
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
    boost::posix_time::time_duration td = us_dst_rules::dst_start_offset();
    return CtiTime(CtiDate(d.day(), d.month(), d.year()), td.hours() + 1, td.minutes(), td.seconds());
}
CtiTime CtiTime::endDST(unsigned int year)
{
    boost::gregorian::date d = us_dst_rules::local_dst_end_day(year);
    boost::posix_time::time_duration td = us_dst_rules::dst_start_offset();
    return CtiTime(CtiDate(d.day(), d.month(), d.year()), td.hours(), td.minutes(), td.seconds()) - 60*60;
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


/*
CtiTime operator + (const CtiTime& t, const int s)
{   
    CtiTime _t(t);
    _t.addSeconds(s);
    return _t;
}

CtiTime operator - (const CtiTime& t, const int s)
{
    CtiTime _t(t);
    _t.addSeconds(-1*s);
    return _t;
} */
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
    s << string(ctime(&t._seconds));
    return s;
}


