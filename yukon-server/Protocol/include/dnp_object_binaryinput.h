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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/03/10 21:05:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_BINARYINPUT_H__
#define __DNP_OBJECT_BINARYINPUT_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"
#include "dnp_object_time.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class BinaryInput : public Object
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
    BinaryInput(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    BinaryInput(int variation);

    enum Variation
    {
        SingleBitPacked = 1,
        WithStatus      = 2
    };

    enum
    {
        Group = 1
    };

    virtual int restore(const unsigned char *buf, int len);
    int restoreBits(const unsigned char *buf, int bitoffset, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};

class BinaryInputChange : public BinaryInput
{
protected:
    Time      _time;
    TimeDelay _timeRelative;

public:
    BinaryInputChange(int variation);

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

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};

}
}
}

#endif  //  #ifndef __DNP_OBJECT_BINARYINPUT_H__
