/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryinput
*
* Class:  DNP Binary Input objects
* Date:   7/5/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/12/26 17:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_BINARYINPUT_H__
#define __DNP_OBJECT_BINARYINPUT_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"
#include "dnp_object_time.h"

class CtiDNPBinaryInput : public CtiDNPObject
{
private:
    union bifu  //  binary in flag union, named for Slick's parsing pleasure
    {
        struct biflags
        {
            unsigned char online        : 1;
            unsigned char restart       : 1;
            unsigned char commlost      : 1;
            unsigned char remoteforced  : 1;
            unsigned char localforced   : 1;
            unsigned char chatterfilter : 1;
            unsigned char reserved      : 1;
            unsigned char state         : 1;
        } flags;

        unsigned char raw;
    } _bi;

protected:
    CtiDNPBinaryInput(int group, int variation);

    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPBinaryInput(int variation);

    enum Variation
    {
        SingleBitPacked = 1,
        WithStatus      = 2
    };

    enum
    {
        Group = 1
    };

    virtual int restore(unsigned char *buf, int len);
    int restoreBits(unsigned char *buf, int bitoffset, int len);
    virtual int serialize(unsigned char *buf);
    virtual int getSerializedLen(void);

    virtual CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};

class CtiDNPBinaryInputChange : public CtiDNPBinaryInput
{
protected:
    CtiDNPTime      _time;
    CtiDNPTimeDelay _timeRelative;

public:
    CtiDNPBinaryInputChange(int variation);

    enum Variation
    {
        WithoutTime      =  1,
        WithTime         =  2,
        WithRelativeTime =  3
    };

    enum
    {
        Group = 2
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);

    CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};

#endif  //  #ifndef __DNP_OBJECT_BINARYINPUT_H__
