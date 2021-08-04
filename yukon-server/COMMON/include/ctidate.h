#pragma once

#include <boost/date_time/gregorian/gregorian.hpp>
#include <string>
#include "dlldefs.h"

class CtiTime;


class IM_EX_CTIBASE CtiDate
{
private:
    boost::gregorian::date bdate;

public:
    enum specialvalues  { neg_infin, pos_infin, not_a_date };

    CtiDate();
    CtiDate(unsigned int day, unsigned int month, unsigned int year);
    CtiDate(unsigned int days, unsigned int year);

    CtiDate(const CtiTime&);

    CtiDate(const CtiDate&);
    CtiDate(specialvalues);

    static unsigned int daysInMonthYear(unsigned month, unsigned year);
    static CtiDate now();

    CtiDate & operator=(const CtiDate&);
    CtiDate & operator+=(const int days);
    CtiDate & operator-=(const int days);

    CtiDate & operator++();
    CtiDate & operator--();

    bool operator <  (const CtiDate& d2) const;
    bool operator <= (const CtiDate& d2) const;
    bool operator >  (const CtiDate& d2) const;
    bool operator >= (const CtiDate& d2) const;
    bool operator == (const CtiDate& d2) const;
    bool operator != (const CtiDate& d2) const;

    unsigned int day() const;
    unsigned int daysFrom1970() const;
    unsigned int dayOfMonth() const;
    unsigned int year() const;
    unsigned int month() const;
    unsigned int weekDay() const;  //  returns 0-6 (sun-sat)

    bool isValid() const;
    bool is_special() const;
    bool is_neg_infinity() const;
    bool is_pos_infinity() const;
    bool isStartOfTime() const;
    bool isEndOfTime() const;

    std::string asString() const;
    std::string asStringMDY() const;
    std::string asStringISO() const;
    std::string weekDayName() const;

    friend CtiDate IM_EX_CTIBASE operator + (const CtiDate& d, const unsigned long s);
    friend CtiDate IM_EX_CTIBASE operator - (const CtiDate& d, const unsigned long s);

    const static boost::gregorian::date StartOfTime;
    const static boost::gregorian::date EndOfTime;
};

namespace Cti {
namespace Date {

    extern IM_EX_CTIBASE std::function<CtiDate()> MakeNowDate;

}
}

