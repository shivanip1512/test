/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
*
* Class:  DNP Analog Input object
* Date:   7/5/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/12/26 17:26:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_ANALOGINPUT_H__
#define __DNP_OBJECT_ANALOGINPUT_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"
#include "dnp_object_time.h"

class CtiDNPAnalogInput : public CtiDNPObject
{
private:
    long _value;

    union aifu  //  analog in flag union, named for Slick's parsing pleasure
    {
        struct aiflags
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
    CtiDNPAnalogInput(int group, int variation);

    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPAnalogInput(int variation);

    enum Variation
    {
        AI32Bit       = 1,
        AI16Bit       = 2,
        AI32BitNoFlag = 3,
        AI16BitNoFlag = 4
    };

    enum
    {
        Group = 30
    };

    virtual int restore(unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf);
    virtual int getSerializedLen(void);

    virtual CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};


//  just inherits the _value and _flags fields, overrides the restore() method and Group enum
class CtiDNPAnalogInputFrozen : public CtiDNPAnalogInput
{
private:
    CtiDNPTime _tof;

public:
    CtiDNPAnalogInputFrozen(int variation);

    enum Variation
    {
        AI32Bit                 = 1,
        AI16Bit                 = 2,
        AI32BitWithTimeOfFreeze = 3,
        AI16BitWithTimeOfFreeze = 4,
        AI32BitNoFlag           = 5,
        AI16BitNoFlag           = 6
    };

    enum
    {
        Group = 31
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};


class CtiDNPAnalogInputChange : public CtiDNPAnalogInput
{
private:
    CtiDNPTime _toc;

public:
    CtiDNPAnalogInputChange(int variation);

    enum Variation
    {
        AI32BitNoTime   = 1,
        AI16BitNoTime   = 2,
        AI32BitWithTime = 3,
        AI16BitWithTime = 4
    };

    enum
    {
        Group = 32
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);

    CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};


class CtiDNPAnalogInputFrozenEvent : public CtiDNPAnalogInput
{
private:
    CtiDNPTime _tofe;

public:
    CtiDNPAnalogInputFrozenEvent(int variation);

    enum Variation
    {
        AI32BitNoTime   = 1,
        AI16BitNoTime   = 2,
        AI32BitWithTime = 3,
        AI16BitWithTime = 4
    };

    enum
    {
        Group = 33
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};

#endif  //  #ifndef __DNP_OBJECT_ANALOGINPUT_H__
