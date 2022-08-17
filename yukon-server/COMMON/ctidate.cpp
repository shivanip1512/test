#include "precompiled.h"

#include "ctitime.h"
#include "ctidate.h"
#include <time.h>
#include "logger.h"

using namespace boost::gregorian;
using std::endl;
using std::string;

const boost::gregorian::date CtiDate::StartOfTime = date(1970, 1, 1);
const boost::gregorian::date CtiDate::EndOfTime = date(2036, 1, 1);

CtiDate::CtiDate() :
  bdate(CtiDate::now().bdate)
{}


CtiDate::CtiDate(unsigned int dd, unsigned int mm, unsigned int yy) :
    bdate(boost::date_time::not_a_date_time)
{
    try
    {
        bdate = date(yy, mm, dd);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_WARN(dout, "exception in CtiDate(day,month,year) (" << dd << "," << mm << "," << yy << ") - setting date to 1/1/1970");
        bdate = date(1970, 1, 1);
    }
}

CtiDate::CtiDate(unsigned int dd, unsigned int yy) :
    bdate(boost::date_time::not_a_date_time)
{
    try
    {
        bdate = date(yy, 1, 1) + date_duration(dd-1);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_WARN(dout, "exception in CtiDate(days,year) (" << dd << "," << yy << ") - setting date to 1/1/1970");
        bdate = date(1970, 1, 1);
    }
}


CtiDate::CtiDate(const CtiTime& ct) :
    bdate(boost::date_time::not_a_date_time)
{
    if( ct.isValid() )
    {
        if( ct.is_neg_infinity() )
        {
            //bdate = date(boost::date_time::neg_infin);
            bdate = date(1970, 1, 1);
        }
        else if(ct.is_pos_infinity())
        {
            //bdate = date(boost::date_time::pos_infin);
            bdate = date(2036, 1, 1);
        }
        else
        {
            tm extracted_time;

            try
            {
                ct.extract(&extracted_time);

                bdate = date(extracted_time.tm_year + 1900, extracted_time.tm_mon + 1, extracted_time.tm_mday);
            }
            catch( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_WARN(dout, "exception in CtiDate(ct) (" << ct << ") - setting date to 1/1/1970");
                bdate = date(1970, 1, 1);
            }
        }
    }
}

CtiDate::CtiDate(const CtiDate& cd) :
    bdate(cd.bdate)
{
}

CtiDate::CtiDate(specialvalues sv) :
    bdate(boost::date_time::not_a_date_time)
{
    switch( sv )
    {
        case neg_infin:     bdate = date(boost::date_time::neg_infin);      break;

        case pos_infin:     bdate = date(boost::date_time::pos_infin);      break;

        //  we're already ::not_a_date_time, no need to reassign it
        /*
        default:
        case not_a_date:    bdate = date(boost::date_time::not_a_date_time);    break;
        */
    }
}

CtiDate& CtiDate::operator=(const CtiDate& cd)
{
    bdate = cd.bdate;
    return *this;
}

CtiDate& CtiDate::operator+=(const int day_offset)
{
    bdate = bdate + date_duration(day_offset);
    return *this;
}

CtiDate& CtiDate::operator-=(const int day_offset)
{
    bdate = bdate - date_duration(day_offset);
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


bool CtiDate::isStartOfTime() const
{
    return bdate == StartOfTime;
}

bool CtiDate::isEndOfTime() const
{
    return bdate == EndOfTime;
}

unsigned int CtiDate::day() const
{
    date_period dp(date(bdate.year(), 1, 1), bdate);

    return dp.length().days() + 1;
}

unsigned int CtiDate::daysFrom1970() const
{
    date_period dp(date(1970, 1, 1), bdate);

    return dp.length().days();
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

string CtiDate::asStringUSFormat() const
{
    //  date format "mm/dd/yyyy" 11 bytes needed
    char time_str[11];
    CtiTime tempTime((CtiDate &)(*this), 0, 0, 0);
    ctitime_t seconds = tempTime.seconds();
    strftime(time_str, 11, "%m/%d/%Y", localtime(&seconds));
    time_str[10] = 0;

    return string(time_str);
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

unsigned int CtiDate::daysInMonthYear(unsigned mm, unsigned yy)
{
    unsigned int days_in_month = 0;

    try
    {
        days_in_month = gregorian_calendar::end_of_month_day(yy, mm);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_WARN(dout, "exception in CtiDate::daysInMonthYear(month,year) (" << mm << "," << yy << ") - returning 0");
    }

    return days_in_month;
}

CtiDate makeNowDate()
{
    //  Copy of boost::date_time::day_clock()
    const time_t currentTime = time(nullptr);

    tm timeStruct;

    localtime_s(&timeStruct, &currentTime);

    return CtiDate(timeStruct.tm_mday, timeStruct.tm_mon + 1, timeStruct.tm_year + 1900);
}

namespace Cti {
namespace Date {

    IM_EX_CTIBASE std::function<CtiDate()> MakeNowDate = makeNowDate;

}
}

CtiDate CtiDate::now()
{
    return Cti::Date::MakeNowDate();
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


bool CtiDate::operator < (const CtiDate& d2) const
{
    return bdate < d2.bdate;
}
bool CtiDate::operator <= (const CtiDate& d2) const
{
    return bdate <= d2.bdate;
}
bool CtiDate::operator > (const CtiDate& d2) const
{
    return bdate > d2.bdate;
}
bool CtiDate::operator >= (const CtiDate& d2) const
{
    return bdate >= d2.bdate;
}
bool CtiDate::operator == (const CtiDate& d2) const
{
    return bdate == d2.bdate;
}
bool CtiDate::operator != (const CtiDate& d2) const
{
    return bdate != d2.bdate;
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


