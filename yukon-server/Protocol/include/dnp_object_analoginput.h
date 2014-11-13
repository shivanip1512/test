#pragma once

#include "dnp_objects.h"
#include "dnp_object_time.h"

namespace Cti {
namespace Protocols {
namespace DNP {


class IM_EX_PROT AnalogInput : public Object
{
private:
    long   _longValue;
    double _doubleValue;

    union aifu  //  analog in flag union, named for Slick's parsing pleasure
    {
        struct
        {
            unsigned char online       : 1;
            unsigned char restart      : 1;
            unsigned char commlost     : 1;
            unsigned char remoteforced : 1;
            unsigned char localforced  : 1;
            unsigned char overrange    : 1;
            unsigned char refcheck     : 1;
            unsigned char reserved     : 1;
        };

        unsigned char raw;
    } _flags;

protected:
    AnalogInput(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    AnalogInput(int variation);

    enum Variation
    {
        AI_32Bit       = 1,
        AI_16Bit       = 2,
        AI_32BitNoFlag = 3,
        AI_16BitNoFlag = 4,
        AI_SingleFloat  = 5,
        AI_DoubleFloat  = 6
    };

    enum
    {
        Group = 30
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;

    void setValue(double value);
    void setOnlineFlag(bool online);
};


//  just inherits the _value and _flags fields, overrides the restore() method and Group enum
class AnalogInputFrozen : public AnalogInput
{
private:
    Time _tof;

public:
    AnalogInputFrozen(int variation);

    enum Variation
    {
        AIF_32Bit                 = 1,
        AIF_16Bit                 = 2,
        AIF_32BitWithTimeOfFreeze = 3,
        AIF_16BitWithTimeOfFreeze = 4,
        AIF_32BitNoFlag           = 5,
        AIF_16BitNoFlag           = 6
    };

    enum
    {
        Group = 31
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
};


class AnalogInputChange : public AnalogInput
{
private:
    Time _toc;

public:
    AnalogInputChange(int variation);

    enum Variation
    {
        AIC_32BitNoTime   = 1,
        AIC_16BitNoTime   = 2,
        AIC_32BitWithTime = 3,
        AIC_16BitWithTime = 4,
        AIC_SingleFloatNoTime   = 5,
        AIC_DoubleFloatNoTime   = 6,
        AIC_SingleFloatWithTime = 7,
        AIC_DoubleFloatWithTime = 8
    };

    enum
    {
        Group = 32
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    void setTime(CtiTime timestamp);

    CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class AnalogInputFrozenEvent : public AnalogInput
{
private:
    Time _tofe;

public:
    AnalogInputFrozenEvent(int variation);

    enum Variation
    {
        AIFE_32BitNoTime   = 1,
        AIFE_16BitNoTime   = 2,
        AIFE_32BitWithTime = 3,
        AIFE_16BitWithTime = 4
    };

    enum
    {
        Group = 33
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
};


}
}
}

