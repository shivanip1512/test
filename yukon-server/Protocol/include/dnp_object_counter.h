/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_counter
*
* Class:  DNP Counter objects
* Date:   7/3/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/03/10 21:04:50 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_COUNTER_H__
#define __DNP_OBJECT_COUNTER_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class Counter : public Object
{
    unsigned long _counter;
    unsigned char _flag;  //  this will be a bitfield struct someday

protected:
    Counter(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    Counter(int variation);

    enum Variation
    {
        Binary32Bit       = 1,
        Binary16Bit       = 2,
        Delta32Bit        = 3,
        Delta16Bit        = 4,
        Binary32BitNoFlag = 5,
        Binary16BitNoFlag = 6,
        Delta32BitNoFlag  = 7,
        Delta16BitNoFlag  = 8
    };

    enum
    {
        Group = 20
    };

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
        Binary32Bit                 =  1,
        Binary16Bit                 =  2,
        Delta32Bit                  =  3,
        Delta16Bit                  =  4,
        Binary32BitWithTimeOfFreeze =  5,
        Binary16BitWithTimeOfFreeze =  6,
        Delta32BitWithTimeOfFreeze  =  7,
        Delta16BitWithTimeOfFreeze  =  8,
        Binary32BitNoFlag           =  9,
        Binary16BitNoFlag           = 10,
        Delta32BitNoFlag            = 11,
        Delta16BitNoFlag            = 12
    };

    enum
    {
        Group = 21
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class CounterChange : public Object
{
protected:

public:
    CounterChange(int variation);

    enum Variation
    {
        Binary32BitNoTime   = 1,
        Binary16BitNoTime   = 2,
        Delta32BitNoTime    = 3,
        Delta16BitNoTime    = 4,
        Binary32BitWithTime = 5,
        Binary16BitWithTime = 6,
        Delta32BitWithTime  = 7,
        Delta16BitWithTime  = 8
    };

    enum
    {
        Group = 22
    };
};


class CounterFrozenEvent : public Object
{
protected:

public:
    CounterFrozenEvent(int variation);

    enum Variation
    {
        Binary32BitNoTime   = 1,
        Binary16BitNoTime   = 2,
        Delta32BitNoTime    = 3,
        Delta16BitNoTime    = 4,
        Binary32BitWithTime = 5,
        Binary16BitWithTime = 6,
        Delta32BitWithTime  = 7,
        Delta16BitWithTime  = 8
    };

    enum
    {
        Group = 23
    };
};

}
}
}

#endif  //  #ifndef __DNP_OBJECT_COUNTER_H__
