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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:44 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_objects.h"
/*
#pragma pack( push, 1 )

struct dnp_control_relay_output_block
{
    //  1 byte
    struct _control_code
    {
        unsigned char code       : 4;
        unsigned char queue      : 1;
        unsigned char clear      : 1;
        unsigned char trip_close : 2;
    } control_code;

    //  2 bytes
    unsigned char count;
    //  6 bytes
    unsigned long on_time;
    //  10 bytes
    unsigned long off_time;
    //  11 bytes
    unsigned char status;
};

struct dnp_analog_output_block_32_bit
{
    long value;
    char status;
};

struct dnp_analog_output_block_16_bit
{
    short value;
    char status;
};


#pragma pack( pop )
*/
class CtiDNPAnalogOutput : public CtiDNPObject
{
protected:

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

    int  restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int  getSerializedLen(void);
};


class CtiDNPAnalogOutputBlock : public CtiDNPObject
{
protected:

public:
    CtiDNPAnalogOutputBlock(int variation);

    enum Variation
    {
        AO32Bit = 1,
        AO16Bit = 2
    };

    enum
    {
        Group = 41
    };

/*    int  restore(unsigned char *buf);
    int serialize(unsigned char *buf);
    int  getSerializedLen(void);*/
};

#endif  //  #ifndef __DNP_OBJECT_ANALOGOUTPUT_H__

