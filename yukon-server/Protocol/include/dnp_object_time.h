/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_time
*
* Class:  DNP Time objects
* Date:   7/8/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2006/02/09 20:42:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_TIME_H__
#define __DNP_OBJECT_TIME_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

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
protected:

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


class TimeDelay : public Object
{
private:
    unsigned long _delay;

protected:

public:
    TimeDelay(int variation);

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

#endif  //  #ifndef __DNP_OBJECT_TIME_H__
