/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_class
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:19:54 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "dnp_object_class.h"
#include "logger.h"

using std::endl;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Class::Class(int variation) : Object(Group, variation)
{

}


int Class::restore(const unsigned char *buf, int len)
{
    return 0;
}


int Class::serialize(unsigned char *buf) const
{
    return 0;
}


int Class::getSerializedLen(void) const
{
    const int variation = getVariation();

    switch(variation)
    {
        case Class0:
        case Class1:
        case Class2:
        case Class3:
        {
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return 0;
}

}
}
}

