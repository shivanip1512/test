#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/07/19 13:41:52 $
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

    if( len < getSerializedLen() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        pos = len;
    }
    else
    {
        pos += restoreVariation(buf + pos, len - pos, getVariation());
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
            //  fall through
        }
        case AI32BitNoFlag:
        {
            _value  = buf[pos++] ;
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AI16Bit:
        {
            _flags.raw = buf[pos++];
            //  fall through
        }
        case AI16BitNoFlag:
        {
            _value  = buf[pos++];
            _value |= buf[pos++] << 8;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            pos = len;
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
            //  fall through
        }
        case AI32BitNoFlag:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >> 24) & 0xff;

            break;
        }

        case AI16Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case AI16BitNoFlag:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return pos;
}


int CtiDNPAnalogInput::getSerializedLen(void)
{
    int retVal;

    switch( getVariation() )
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


void CtiDNPAnalogInput::getPoint( RWTPtrSlist< CtiMessage > &objPoints )
{
    CtiPointDataMsg *tmpMsg;

    double val;
    int quality;

    switch(getVariation())
    {
        case AI32Bit:
        {
            val = _value;
            break;
        }

        case AI16Bit:
        {
            val = _value;
            break;
        }

        case AI32BitNoFlag:
        {
            val = _value;
            break;
        }

        case AI16BitNoFlag:
        {
            val = _value;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

/*    UnintializedQuality = 0,
    InitDefaultQuality,
    InitLastKnownQuality,
    NonUpdatedQuality,
    ManualQuality,
    NormalQuality,
    ExceedsLowQuality,
    ExceedsHighQuality,
    AbnormalQuality,
    UnknownQuality,
    InvalidQuality,
    PartialIntervalQuality,
    DeviceFillerQuality,
    QuestionableQuality,
    OverflowQuality,
    PowerfailQuality,
    UnreasonableQuality

    if( _flags.aiflags.remoteforced )
    {

    }*/

    tmpMsg = new CtiPointDataMsg(0, val, NormalQuality, AnalogPointType);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Analog input, value " << val << endl;
    }

    if( tmpMsg != NULL )
    {
        objPoints.append(tmpMsg);
    }
}


//  ---  ANALOG INPUT FROZEN  ---

CtiDNPAnalogInputFrozen::CtiDNPAnalogInputFrozen(int variation) : CtiDNPAnalogInput(variation),
    _tof(CtiDNPTime::TimeAndDate)
{

}


int CtiDNPAnalogInputFrozen::restore(unsigned char *buf, int len)
{
    int pos = 0;

    if( len < getSerializedLen() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        pos = len;
    }
    else
    {
        switch(getVariation())
        {
            case AI16Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16Bit);
                break;
            }

            case AI16BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16BitNoFlag);
                break;
            }

            case AI16BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16Bit);
                pos += _tof.restore(buf + pos, len - pos);
                break;
            }

            case AI32Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI32Bit);
                break;
            }

            case AI32BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16BitNoFlag);
                break;
            }

            case AI32BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI32Bit);
                pos += _tof.restore(buf + pos, len - pos);
                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                break;
            }
        }
    }

    return pos;
}


int CtiDNPAnalogInputFrozen::serialize(unsigned char *buf)
{
    int pos = 0;

    switch(getVariation())
    {
        case AI16Bit:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16Bit);
            break;
        }

        case AI16BitNoFlag:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16BitNoFlag);
            break;
        }

        case AI16BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16Bit);
            pos += _tof.serialize(buf + pos);
            break;
        }

        case AI32Bit:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI32Bit);
            break;
        }

        case AI32BitNoFlag:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16BitNoFlag);
            break;
        }

        case AI32BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI32Bit);
            pos += _tof.serialize(buf + pos);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    return pos;
}


int CtiDNPAnalogInputFrozen::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case AI16Bit:
        {
            retVal = 3;
            break;
        }

        case AI16BitNoFlag:
        {
            retVal = 2;
            break;
        }

        case AI16BitWithTimeOfFreeze:
        {
            retVal = 9;
            break;
        }

        case AI32Bit:
        {
            retVal = 5;
            break;
        }

        case AI32BitNoFlag:
        {
            retVal = 4;
            break;
        }

        case AI32BitWithTimeOfFreeze:
        {
            retVal = 11;
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


//  ---  ANALOG INPUT CHANGE  ---

CtiDNPAnalogInputChange::CtiDNPAnalogInputChange(int variation) : CtiDNPObject(Group, variation),
    _toc(CtiDNPTime::TimeAndDate)
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

CtiDNPAnalogInputFrozenEvent::CtiDNPAnalogInputFrozenEvent(int variation) : CtiDNPObject(Group, variation),
    _tofe(CtiDNPTime::TimeAndDate)
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

