#pragma once

#include "dnp_objects.h"
#include "dnp_object_time.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class IM_EX_PROT Counter : public Object
{
    unsigned long _counter;
    union
    {
        struct
        {
            unsigned char online        : 1;
            unsigned char restart       : 1;
            unsigned char commlost      : 1;
            unsigned char remoteforced  : 1;
            unsigned char localforced   : 1;
            unsigned char rollover      : 1;
            unsigned char discontinuity : 1;
            unsigned char reserved      : 1;
        };

        unsigned char raw;
    } _flags;

protected:
    Counter(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    Counter(int variation);

    enum Variation
    {
        C_Binary32Bit       = 1,
        C_Binary16Bit       = 2,
        C_Delta32Bit        = 3,
        C_Delta16Bit        = 4,
        C_Binary32BitNoFlag = 5,
        C_Binary16BitNoFlag = 6,
        C_Delta32BitNoFlag  = 7,
        C_Delta16BitNoFlag  = 8
    };

    enum
    {
        Group = 20
    };
    void setValue(long value);
    void setOnlineFlag(bool online);
    unsigned long getValue() const;
    unsigned char getFlags() const;

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;


    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class CounterFrozen : public Counter
{
protected:

public:
    CounterFrozen(int variation);

    enum Variation
    {
        CF_Binary32Bit                 =  1,
        CF_Binary16Bit                 =  2,
        CF_Delta32Bit                  =  3,
        CF_Delta16Bit                  =  4,
        CF_Binary32BitWithTimeOfFreeze =  5,
        CF_Binary16BitWithTimeOfFreeze =  6,
        CF_Delta32BitWithTimeOfFreeze  =  7,
        CF_Delta16BitWithTimeOfFreeze  =  8,
        CF_Binary32BitNoFlag           =  9,
        CF_Binary16BitNoFlag           = 10,
        CF_Delta32BitNoFlag            = 11,
        CF_Delta16BitNoFlag            = 12
    };

    enum
    {
        Group = 21
    };

    enum
    {
        //  Frozen counters are offset by this amount when they are returned as normal pulse accumulators
        CounterFrozenOffset = 20000
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class CounterEvent : public Counter
{
protected:

    Time _toc;

public:
    CounterEvent(int variation);

    enum Variation
    {
        CE_Binary32BitNoTime   = 1,
        CE_Binary16BitNoTime   = 2,
        CE_Delta32BitNoTime    = 3,
        CE_Delta16BitNoTime    = 4,
        CE_Binary32BitWithTime = 5,
        CE_Binary16BitWithTime = 6,
        CE_Delta32BitWithTime  = 7,
        CE_Delta16BitWithTime  = 8
    };

    enum
    {
        Group = 22
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;
    virtual int serializeVariation(unsigned char *buf, int variation) const;

    void setTime(CtiTime timestamp);

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class CounterFrozenEvent : public Object
{
protected:

public:
    CounterFrozenEvent(int variation);

    enum Variation
    {
        CFE_Binary32BitNoTime   = 1,
        CFE_Binary16BitNoTime   = 2,
        CFE_Delta32BitNoTime    = 3,
        CFE_Delta16BitNoTime    = 4,
        CFE_Binary32BitWithTime = 5,
        CFE_Binary16BitWithTime = 6,
        CFE_Delta32BitWithTime  = 7,
        CFE_Delta16BitWithTime  = 8
    };

    enum
    {
        Group = 23
    };
};

}
}
}
