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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/12/26 17:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_COUNTER_H__
#define __DNP_OBJECT_COUNTER_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

class CtiDNPCounter : public CtiDNPObject
{
    unsigned long _counter;
    unsigned char _flag;  //  this will be a bitfield struct someday

protected:
    CtiDNPCounter(int group, int variation);

    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPCounter(int variation);

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

    virtual int restore(unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf);
    virtual int getSerializedLen(void);

    CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};


class CtiDNPCounterFrozen : public CtiDNPObject
{
protected:

public:
    CtiDNPCounterFrozen(int variation);

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
};


class CtiDNPCounterChange : public CtiDNPObject
{
protected:

public:
    CtiDNPCounterChange(int variation);

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


class CtiDNPCounterFrozenEvent : public CtiDNPObject
{
protected:

public:
    CtiDNPCounterFrozenEvent(int variation);

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

#endif  //  #ifndef __DNP_OBJECT_COUNTER_H__
