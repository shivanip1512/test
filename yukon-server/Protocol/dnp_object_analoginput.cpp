#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
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

#include "dnp_object_analoginput.h"
#include "logger.h"


//  ---  ANALOG INPUT  ---

CtiDNPAnalogInput::CtiDNPAnalogInput(int variation) : CtiDNPObject(Group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


int CtiDNPAnalogInput::restore(unsigned char *buf, int len)
{
    int pos = 0;

    if( len >= getSerializedLen() )
    {
        pos = restoreVariation(buf, len, getVariation());
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        pos = len;
    }

    return pos;
}


int CtiDNPAnalogInput::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AI32Bit:
        {
            _flags.raw = buf[pos++];
        }
        case AI32BitNoFlag:
        {
            _value = buf[pos++];
            _value = buf[pos++] | (_value << 8);
            _value = buf[pos++] | (_value << 8);
            _value = buf[pos++] | (_value << 8);

            break;
        }

        case AI16Bit:
        {
            _flags.raw = buf[pos++];
        }
        case AI16BitNoFlag:
        {
            _value = buf[pos++];
            _value = buf[pos++] | (_value << 8);

            break;
        }
    }

    return pos;
}


int CtiDNPAnalogInput::serialize(unsigned char *buf)
{
    return serializeVariation(buf, getVariation());
}


int CtiDNPAnalogInput::serializeVariation(unsigned char *buf, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AI32Bit:
        {
            buf[pos++] = _flags.raw;
        }
        case AI32BitNoFlag:
        {
            buf[pos++] = (_value >> 24) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] =  _value        & 0xff;

            break;
        }

        case AI16Bit:
        {
            buf[pos++] = _flags.raw;
        }
        case AI16BitNoFlag:
        {
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] =  _value        & 0xff;

            break;
        }
    }

    return pos;
}


int CtiDNPAnalogInput::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AI32Bit:
        {
            retVal = 5;

            break;
        }
        case AI16Bit:
        {
            retVal = 3;

            break;
        }
        case AI32BitNoFlag:
        {
            retVal = 4;

            break;
        }
        case AI16BitNoFlag:
        {
            retVal = 2;

            break;
        }
    }

    return retVal;
}


//  ---  ANALOG INPUT FROZEN  ---

CtiDNPAnalogInputFrozen::CtiDNPAnalogInputFrozen(int variation) : CtiDNPAnalogInput(variation)
{

}


int CtiDNPAnalogInputFrozen::restore(unsigned char *buf, int len)
{
    int pos = 0;

    if( len >= getSerializedLen() )
    {
        switch(getVariation())
        {
            case AI16Bit:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI16Bit);

                break;
            }
            case AI16BitNoFlag:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI16BitNoFlag);

                break;
            }
            case AI16BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI16Bit);
                //  and more restore here whee!

                break;
            }
            case AI32Bit:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI32Bit);

                break;
            }
            case AI32BitNoFlag:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI16BitNoFlag);

                break;
            }
            case AI32BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf, len, CtiDNPAnalogInput::AI32Bit);
                //  and restore more here whee!

                break;
            }
        }
    }

    return pos;
}


int CtiDNPAnalogInputFrozen::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPAnalogInputFrozen::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AI16Bit:
        {
            retVal = 0;

            break;
        }
        case AI16BitNoFlag:
        {
            retVal = 0;

            break;
        }
        case AI16BitWithTimeOfFreeze:
        {
            retVal = 0;

            break;
        }
        case AI32Bit:
        {
            retVal = 0;

            break;
        }
        case AI32BitNoFlag:
        {
            retVal = 0;

            break;
        }
        case AI32BitWithTimeOfFreeze:
        {
            retVal = 0;

            break;
        }
    }

    return retVal;
}


//  ---  ANALOG INPUT CHANGE  ---

CtiDNPAnalogInputChange::CtiDNPAnalogInputChange(int variation) : CtiDNPObject(Group, variation)
{

}


int CtiDNPAnalogInputChange::restore(unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPAnalogInputChange::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPAnalogInputChange::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AI16BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AI16BitWithTime:
        {
            retVal = 0;

            break;
        }
        case AI32BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AI32BitWithTime:
        {
            retVal = 0;

            break;
        }
    }

    return retVal;
}


//  ---  ANALOG INPUT FROZEN EVENT  ---

CtiDNPAnalogInputFrozenEvent::CtiDNPAnalogInputFrozenEvent(int variation) : CtiDNPObject(Group, variation)
{

}


int CtiDNPAnalogInputFrozenEvent::restore(unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPAnalogInputFrozenEvent::serialize(unsigned char *buf)
{
    return 0;
}


int CtiDNPAnalogInputFrozenEvent::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AI16BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AI16BitWithTime:
        {
            retVal = 0;

            break;
        }
        case AI32BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AI32BitWithTime:
        {
            retVal = 0;

            break;
        }
    }

    return retVal;
}

