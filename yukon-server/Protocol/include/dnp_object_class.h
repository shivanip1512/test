#pragma warning( disable : 4786)
#ifndef __DNP_OBJECT_CLASS_H__
#define __DNP_OBJECT_CLASS_H__

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_class
*
* Class:  DNP Class objects
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

class CtiDNPClass : public CtiDNPObject
{
protected:

public:
    CtiDNPClass(int variation);

    enum Variation
    {
        Class0 = 1,
        Class1 = 2,
        Class2 = 3,
        Class3 = 4
    };

    enum Group
    {
        Group = 60
    };

    int  restore(unsigned char *buf, int len);
    int serialize(unsigned char *buf);
    int  getSerializedLen(void);
};

#endif  //  #ifndef __DNP_OBJECT_CLASS_H__

