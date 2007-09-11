/*
 * class CtiTime
 *  
 * Author: Jian Liu 
 * Date: 08/10/2005 15:37:00 
 * 
 * 
 * This class is to replace the RWTime class.
 * It uses seconds from the ctime epoch to store the current time.
 * This class only can handle localtime and UTC time. It can not do other time transform
 *
 * The class is not thread-safe.
 *
 * Static methods localtime_r and gmtime_r are thread-safe, and they use thread-specific
 * storage to handle struct tm*
 *
 * the whole class is based on ctime, except the dst rules and synchronization 
 */


#ifndef _CTITIME_H_
#define _CTITIME_H_

#include <string>
#include <ostream>
#include <time.h>
#include "dlldefs.h"

#define CTIEOT (LONG_MAX - 86400 * 2)
#define BEGIN_TIME "1970,Jan,01" //GMT

using std::string;

class CtiDate;

typedef time_t ctitime_t;


class IM_EX_CTIBASE CtiTime{
private:
    ctitime_t maketm(CtiDate& d, unsigned hour = 0, unsigned minute = 0, unsigned second = 0);
    ctitime_t _seconds;
    

    //boost::mutex _secs_mutex;
public:
    enum specialvalues  {neg_infin, pos_infin, not_a_time};

    CtiTime();
    CtiTime(specialvalues);
    CtiTime(unsigned long s);
    CtiTime(unsigned hour, unsigned minute, unsigned int second=0);
    CtiTime(CtiDate& d, unsigned hour = 0, unsigned minute = 0, unsigned second = 0);
    CtiTime(struct tm*);

    CtiTime(const CtiTime&);

    CtiTime& addSeconds(const int secs);
    CtiTime& addMinutes(const int mins);

    CtiTime& operator = (const CtiTime&);
    CtiTime& operator += (const int secs);
    CtiTime& operator -= (const int secs);


    CtiTime  asGMT() const;

    int day() const;
    int second() const;
    ctitime_t seconds() const;
    int minute() const;
    int minuteGMT()const;
    int hour() const;
    int hourGMT() const; 
    CtiDate date() const;

    void extract(struct tm*) const; 
    unsigned long toRwSeconds() const;

    bool isDST() const;
    bool isValid() const;
    bool is_special() const;
    bool is_neg_infinity() const;
    bool is_pos_infinity() const;


    string asString() const;

    CtiTime addDays(const int days, bool DSTflag = true);

    static CtiTime now();
    void resetToNow();
    static CtiTime beginDST(unsigned year);
    static CtiTime endDST(unsigned int builyear); 
    static long findTZ();
    static struct tm* gmtime_r(const time_t *tod);
    static struct tm* localtime_r(const time_t *tod);
    
    //friend CtiTime IM_EX_CTIBASE operator + (const CtiTime& t, const int s);
    //friend CtiTime IM_EX_CTIBASE operator - (const CtiTime& t, const int s);
    
    friend CtiTime IM_EX_CTIBASE operator + (const CtiTime& t, const unsigned long s);    
    friend CtiTime IM_EX_CTIBASE operator - (const CtiTime& t, const unsigned long s);

    friend bool IM_EX_CTIBASE operator < (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator <= (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator > (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator >= (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator == (const CtiTime& t1, const CtiTime& t2);
    friend bool IM_EX_CTIBASE operator != (const CtiTime& t1, const CtiTime& t2);


    friend IM_EX_CTIBASE std::ostream&  operator<<(std::ostream& s, const CtiTime& t);

    //friend RWvostream& operator<<(RWvostream&, const RWTime& t);

    //friend RWFile& operator<<(RWFile&,     const RWTime& t);

    //friend RWvistream& operator>>(RWvistream&, RWTime& t);

    //friend RWFile& operator>>(RWFile&,     RWTime& t);



};


#endif _CTITIME_H_

