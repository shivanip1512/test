
#include "yukon.h"
#include "ctitime.h"
#include "ctidate.h"
#include <time.h>


using namespace boost::gregorian;


CtiDate::CtiDate() :
  bdate(day_clock::local_day())
{}


CtiDate::CtiDate(unsigned int day, unsigned int month, unsigned int year) :
  bdate(year, month, day)
{
    //bdate had a month range of 1-12 so you muct remember when using other time containers, 
    //and creating a ctidate ( like  'tm' )  they may be on a 0-11 scale    'tm' also stores the year  -1900
    if ( month ==0 || month > 12 ){
		CtiLockGuard<CtiLogger> doubt_guard(dout);
		dout << CtiTime() << " **** Checkpoint **** Bad Month Passed to CtiDate" << __FILE__ << " (" << __LINE__ << ")" << endl;
	}
}
CtiDate::CtiDate(unsigned int days, unsigned int year) :
  bdate(year, 1, 1)
{
    date_duration dd(days-1);
    bdate = bdate + dd;
}


CtiDate::CtiDate(const CtiTime& ct) :
  bdate(boost::date_time::not_a_date_time)
{
    if(ct.isValid())
    { 
        if(ct.is_neg_infinity()){
            bdate = date(boost::date_time::neg_infin);
        } else if(ct.is_pos_infinity()){
            bdate = date(boost::date_time::pos_infin);
        } else {
            struct tm ctime;
            ct.extract(&ctime);
            bdate = date(ctime.tm_year + 1900, ctime.tm_mon + 1, ctime.tm_mday);
        }
    }
}

CtiDate::CtiDate(const CtiDate& cd) :
  bdate(cd.bdate)
{}

CtiDate::CtiDate(specialvalues sv) :
  bdate(boost::date_time::not_a_date_time)
{
    if(sv == CtiDate::neg_infin)
    {
        bdate = date(boost::date_time::neg_infin);
    }
    else if(sv == CtiDate::pos_infin)
    {
        bdate= date(boost::date_time::pos_infin);
    }
    else if(sv == CtiDate::not_a_date)
    {
        bdate= date(boost::date_time::not_a_date_time);
    }
}



CtiDate& CtiDate::operator=(const CtiDate& cd)
{
    bdate = cd.bdate;
    return *this;
}

CtiDate& CtiDate::operator+=(const int days)
{
    bdate = bdate + date_duration(days);
    return *this;
}

CtiDate& CtiDate::operator-=(const int days)
{
    bdate = bdate - date_duration(days);
    return *this;
}

bool CtiDate::isValid() const
{
    return !bdate.is_not_a_date();
}

bool CtiDate::is_special() const
{
    return bdate.is_not_a_date()||bdate.is_infinity();
}


bool CtiDate::is_neg_infinity() const
{
    return bdate.is_neg_infinity();
}

bool CtiDate::is_pos_infinity() const
{
    return bdate.is_pos_infinity();
}


unsigned int CtiDate::day() const 
{
    date_period dp(date(bdate.year(), 1, 1), bdate);
    return dp.length().days() + 1;
}
unsigned int CtiDate::dayOfMonth() const
{
    return bdate.day();
}
unsigned int CtiDate::year() const 
{
    return bdate.year();
}
unsigned int CtiDate::month() const 
{
    return bdate.month();
}

string CtiDate::asString() const 
{
    return to_simple_string(bdate);
}

string CtiDate::weekDayName() const 
{
    return bdate.day_of_week().as_long_string();
}

unsigned int CtiDate::weekDay() const
{
    date::ymd_type ymd = bdate.year_month_day();
    return gregorian_calendar::day_of_week(ymd);
}

unsigned int CtiDate::firstDayOfMonth() const
{ 
    date_period dp(date(bdate.year(), 1, 1), date(bdate.year(), bdate.month(), 1));
    return dp.length().days() + 1;
}


unsigned int CtiDate::daysInMonthYear(unsigned month, unsigned year)
{
    return gregorian_calendar::end_of_month_day(year, month);
}

CtiDate CtiDate::now()
{
    return CtiDate();
}
CtiDate& CtiDate::toUTCdate()
{
    struct tm ctime;
    CtiTime ct(*this);
    ct.toUTCtime();
    ct.extract(&ctime);
    bdate = date(ctime.tm_mday, ctime.tm_mon, ctime.tm_year); 

    return *this;
}

CtiDate& CtiDate::operator++()
{
    bdate = bdate + date_duration(1);
    return *this;
}

CtiDate& CtiDate::operator--()
{
    bdate = bdate - date_duration(1); 
    return *this;
}


bool operator < (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate < d2.bdate;
}
bool operator <= (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate <= d2.bdate;
}
bool operator > (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate > d2.bdate;
}
bool operator >= (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate >= d2.bdate;
}
bool operator == (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate == d2.bdate;
}
bool operator != (const CtiDate& d1, const CtiDate& d2)
{
    return d1.bdate != d2.bdate;
}



CtiDate operator + (const CtiDate& d, const unsigned long s)
{   
    CtiDate _date;
    _date.bdate = d.bdate + date_duration(s);
    return _date;
}
//CtiDate operator + (const unsigned long s, const CtiDate& d);
CtiDate operator - (const CtiDate& d, const unsigned long s)
{
    CtiDate _date;
    _date.bdate = d.bdate - date_duration(s);
    return _date;
}


