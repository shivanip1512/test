#pragma once

#include <string>
#include <ostream>
#include <time.h>
#include "dlldefs.h"


class CtiDate;

typedef time_t ctitime_t;


class IM_EX_CTIBASE CtiTime{
private:
    enum timeFormat{LOCAL_TIMEZONE, GMT_TIMEZONE};
    ctitime_t maketm(const CtiDate& d, unsigned hour = 0, unsigned minute = 0, unsigned second = 0);
    ctitime_t _seconds;
    std::string asString(timeFormat type) const;

    //boost::mutex _secs_mutex;
public:
    enum specialvalues  {neg_infin, pos_infin, not_a_time};

    CtiTime();
    CtiTime(specialvalues);
    CtiTime(unsigned long s);
    CtiTime(unsigned hour, unsigned minute, unsigned int second=0);
    CtiTime(const CtiDate& d, unsigned hour = 0, unsigned minute = 0, unsigned second = 0);
    CtiTime(struct tm*);

    CtiTime(const CtiTime&);

    CtiTime& addSeconds(const int secs);
    CtiTime& addMinutes(const int mins);

    CtiTime& operator = (const CtiTime&);
    CtiTime& operator += (const int secs);
    CtiTime& operator -= (const int secs);

    static CtiTime fromLocalSeconds(const unsigned long local_seconds);

    int day() const;
    int dayGMT() const;
    int second() const;
    int secondGMT() const;
    ctitime_t seconds() const;
    int minute() const;
    int minuteGMT()const;
    int hour() const;
    int hourGMT() const;
    CtiDate date() const;
    CtiDate dateGMT() const;

    void extract(struct tm*) const;
    unsigned long toRwSeconds() const;

    bool isDST() const;
    bool isValid() const;
    bool is_special() const;
    bool is_neg_infinity() const;
    bool is_pos_infinity() const;

    long secondOffsetToGMT() const;

    std::string asString() const;
    std::string asGMTString() const;

    CtiTime addDays(const int days, bool DSTflag = true);

    static CtiTime now();
    void resetToNow();
    static CtiTime beginDST(unsigned year);
    static CtiTime endDST(unsigned int builyear);
    static struct tm* gmtime_r(const time_t *tod);
    static struct tm* localtime_r(const time_t *tod);

    friend CtiTime IM_EX_CTIBASE operator + (const CtiTime& t, const unsigned long s);
    friend CtiTime IM_EX_CTIBASE operator - (const CtiTime& t, const unsigned long s);

    friend bool IM_EX_CTIBASE operator < (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator <= (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator > (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator >= (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator == (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator != (const CtiTime& t1, const CtiTime& t2);


    friend IM_EX_CTIBASE std::ostream&  operator<<(std::ostream& s, const CtiTime& t);

};

