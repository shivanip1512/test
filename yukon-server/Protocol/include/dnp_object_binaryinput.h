#pragma warning( disable : 4786)
#ifndef __DNP_OBJECT_BINARYINPUT_H__
#define __DNP_OBJECT_BINARYINPUT_H__

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:44 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_objects.h"

class CtiDNPBinaryInput : public CtiDNPObject
{
protected:

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

    int  restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);
};

class CtiDNPBinaryInputChange : public CtiDNPBinaryInput
{
protected:

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
};

#endif  //  #ifndef __DNP_OBJECT_BINARYINPUT_H__
