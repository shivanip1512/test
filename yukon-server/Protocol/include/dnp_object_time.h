#pragma warning( disable : 4786)
#ifndef __DNP_OBJECT_TIME_H__
#define __DNP_OBJECT_TIME_H__

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:44 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_objects.h"

class CtiDNPTime : public CtiDNPObject
{
private:
    double _seconds, _milliseconds;
    double _interval;

protected:
    CtiDNPTime(int group, int variation);

    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPTime(int variation);

    enum Variation
    {
        TimeAndDate                  = 1,
        TimeAndDateWithInterval      = 2
    };

    enum
    {
        Group = 50
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};


class CtiDNPTimeCTO : public CtiDNPTime
{
protected:

public:
    CtiDNPTimeCTO(int variation);

    enum Variation
    {
        TimeAndDateCTO               = 1,
        TimeAndDateCTOUnsynchronized = 2
    };

    enum
    {
        Group = 51
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};


class CtiDNPTimeDelay : public CtiDNPObject
{
protected:

public:
    CtiDNPTimeDelay(int variation);

    enum Variation
    {
        Coarse = 1,
        Fine   = 2
    };

    enum
    {
        Group = 52
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};


#endif  //  #ifndef __DNP_OBJECT_TIME_H__
