#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryinput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_object_binaryinput.h"
#include "logger.h"


CtiDNPBinaryInput::CtiDNPBinaryInput(int variation) : CtiDNPObject(Group, variation)
{

}


int CtiDNPBinaryInput::restore(unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPBinaryInput::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPBinaryInput::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case SingleBitPacked:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " Single-bit packed binary input not supported yet " << endl;
            }

            retVal = 0;

            break;
        }

        case WithStatus:
        {
            retVal = 1;

            break;
        }
    }

    return retVal;
}


CtiDNPBinaryInputChange::CtiDNPBinaryInputChange(int variation) : CtiDNPBinaryInput(variation)
{

}

