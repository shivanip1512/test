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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/12/26 17:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_ANALOGOUTPUT_H__
#define __DNP_OBJECT_ANALOGOUTPUT_H__
#pragma warning( disable : 4786)


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
    CtiDNPAnalogOutput(int group, int variation);

    int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

    void setValue(long value);

public:
    CtiDNPAnalogOutput(int variation);

    enum variation
    {
        AO32Bit = 1,
        AO16Bit = 2
    };

    enum group
    {
        Group = 40
    };

    virtual int restore(unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf);
    virtual int getSerializedLen(void);

    virtual CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};


class CtiDNPAnalogOutputBlock : public CtiDNPObject
{
private:
    long _value;

    unsigned char _status;

protected:
int restoreVariation(unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation);

public:
    CtiDNPAnalogOutputBlock(int variation);

    enum variation
    {
        AOB32Bit = 1,
        AOB16Bit = 2
    };

    enum group
    {
        Group = 41
    };

    int restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);

    void setControl(long value);
};

#endif  //  #ifndef __DNP_OBJECT_ANALOGOUTPUT_H__

