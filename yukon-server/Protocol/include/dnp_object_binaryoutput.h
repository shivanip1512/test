#pragma warning( disable : 4786)
#ifndef __DNP_OBJECT_BINARYOUTPUT_H__
#define __DNP_OBJECT_BINARYOUTPUT_H__

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryoutput
*
* Class:  DNP Binary Output objects
* Date:   7/3/2002
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

#pragma pack( pop )
*/
class CtiDNPBinaryOutput : public CtiDNPObject
{
protected:

public:
    CtiDNPBinaryOutput(int variation);

    enum Variation
    {
        SingleBit  = 1,
        WithStatus = 2
    };

    enum
    {
        Group = 0x10
    };

    int  restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int  getSerializedLen(void);
};


class CtiDNPBinaryOutputControl : public CtiDNPObject
{
protected:

public:
    CtiDNPBinaryOutputControl(int variation);

    enum Variation
    {
        ControlRelayOutputBlock = 1,
        PatternControlBlock     = 2,
        PatternMask             = 3
    };

    enum
    {
        Group = 0x12
    };
};

#endif  //  #ifndef __DNP_OBJECT_BINARYOUTPUT_H__
