/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analogoutput
*
* Class:  DNP Analog Output object
* Date:   7/5/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/03/10 21:05:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_ANALOGOUTPUT_H__
#define __DNP_OBJECT_ANALOGOUTPUT_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class AnalogOutput : public Object
{
private:
    long _value;

    union aofu //  analog out flag union, only for Slick's parsing pleasure
    {
        struct aoflags
        {
            unsigned char online       : 1;
            unsigned char restart      : 1;
            unsigned char commlost     : 1;
            unsigned char remoteforced : 1;
            unsigned char reserved     : 4;
        };

        unsigned char raw;
    } _flags;

protected:
    AnalogOutput(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

    void setValue(long value);

public:
    AnalogOutput(int variation);

    enum variation
    {
        AO32Bit = 1,
        AO16Bit = 2
    };

    enum group
    {
        Group = 40
    };

    enum
    {
        //  Analog outputs are offset by this amount when they are returned as normal analogs
        AnalogOutputOffset = 10000
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class AnalogOutputBlock : public Object
{
private:
    long _value;

    unsigned char _status;

protected:
int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    AnalogOutputBlock(int variation);

    enum variation
    {
        AOB32Bit = 1,
        AOB16Bit = 2
    };

    enum group
    {
        Group = 41
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    void setControl(long value);
};

}
}
}

#endif  //  #ifndef __DNP_OBJECT_ANALOGOUTPUT_H__

