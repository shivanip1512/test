#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analogoutput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/16 13:57:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_object_analogoutput.h"
#include "logger.h"

CtiDNPAnalogOutput::CtiDNPAnalogOutput(int variation) : CtiDNPObject(Group, variation)
{

}

int CtiDNPAnalogOutput::restore(unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPAnalogOutput::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPAnalogOutput::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AO32Bit:
        {
            retVal = 0;

            break;
        }
        case AO16Bit:
        {
            retVal = 0;

            break;
        }
    }

    return retVal;
}


CtiDNPAnalogOutputBlock::CtiDNPAnalogOutputBlock(int variation) : CtiDNPObject(Group, variation)
{

}

