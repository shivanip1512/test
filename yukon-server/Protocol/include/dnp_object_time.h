#pragma once

#include "dnp_objects.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class Time : public Object
{
private:
    double _seconds, _milliseconds;
    double _interval;

protected:
    Time(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    Time(int variation);

    enum Variation
    {
        T_TimeAndDate                  = 1,
        T_TimeAndDateWithInterval      = 2
    };

    enum
    {
        Group = 50
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    double getSeconds() const;
    double getMilliseconds() const;

    void setSeconds( double seconds );
    void setMilliseconds( double millis );
};


class TimeCTO : public Time
{
public:
    TimeCTO(int variation);

    TimeCTO(const TimeCTO &src);

    enum Variation
    {
        TC_TimeAndDateCTO               = 1,
        TC_TimeAndDateCTOUnsynchronized = 2
    };

    enum
    {
        Group = 51
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
};


class IM_EX_PROT TimeDelay : public Object
{
    unsigned long _delay;

public:
    TimeDelay(int variation);
    TimeDelay(std::chrono::milliseconds fineDelay);
    TimeDelay(std::chrono::seconds coarseDelay);

    enum Variation
    {
        TD_Coarse = 1,
        TD_Fine   = 2
    };

    enum
    {
        Group = 52
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    double getSeconds() const;
    double getMilliseconds() const;
};

}
}
}
