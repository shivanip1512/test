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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/03/10 21:05:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_CLASS_H__
#define __DNP_OBJECT_CLASS_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class Class : public Object
{
protected:

public:
    Class(int variation);

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

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
};

}
}
}

#endif  //  #ifndef __DNP_OBJECT_CLASS_H__

