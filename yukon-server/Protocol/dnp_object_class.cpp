/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_class
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_class.h"
#include "logger.h"

CtiDNPClass::CtiDNPClass(int variation) : CtiDNPObject(Group, variation)
{

}


int CtiDNPClass::restore(unsigned char *buf, int len)
{
    return 0;
}


int CtiDNPClass::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPClass::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case Class0:
        case Class1:
        case Class2:
        case Class3:
        {
            retVal = 0;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;
            break;
        }
    }

    return retVal;
}


