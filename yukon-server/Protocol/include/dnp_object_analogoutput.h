#pragma warning( disable : 4786)
#ifndef __DNP_OBJECT_ANALOGOUTPUT_H__
#define __DNP_OBJECT_ANALOGOUTPUT_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/07/19 13:41:54 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_objects.h"


class CtiDNPAnalogOutput : public CtiDNPObject
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
    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPAnalogOutput(int variation);

    enum Variation
    {
        AO32Bit = 1,
        AO16Bit = 2
    };

    enum
    {
        Group = 40
    };

    virtual int restore(unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf);
    virtual int getSerializedLen(void);

    void getPoint( RWTPtrSlist< CtiMessage > &objPoints );
};


class CtiDNPAnalogOutputBlock : public CtiDNPAnalogOutput
{
private:
    unsigned char _status;

protected:

public:
    CtiDNPAnalogOutputBlock(int variation);

    enum Variation
    {
        AOB32Bit = 1,
        AOB16Bit = 2
    };

    enum
    {
        Group = 41
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};

#endif  //  #ifndef __DNP_OBJECT_ANALOGOUTPUT_H__

