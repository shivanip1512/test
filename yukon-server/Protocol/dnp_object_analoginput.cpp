/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/03/10 21:25:29 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_analoginput.h"
#include "logger.h"


namespace Cti       {
namespace Protocol  {
namespace DNP       {

//  ---  ANALOG INPUT  ---

AnalogInput::AnalogInput(int group, int variation) : Object(group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


AnalogInput::AnalogInput(int variation) : Object(Group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


int AnalogInput::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    if( len < getSerializedLen() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        pos = len;
        _valid = false;
    }
    else
    {
        pos += restoreVariation(buf + pos, len - pos, getVariation());
    }

    return pos;
}


int AnalogInput::restoreVariation(const unsigned char *buf, int len, int variation)
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
            _value = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _value |= buf[pos++] ;
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
            short tmpValue;

            tmpValue = 0;

            //  these 16 bits bytes will fill up the short, including the sign bit...
            tmpValue |= buf[pos++];
            tmpValue |= buf[pos++] << 8;

            //  ...  so we can use the compiler's cast to convert it over
            _value = tmpValue;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            pos = len;
            _valid = false;
        }
    }

    return pos;
}


int AnalogInput::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());
}


int AnalogInput::serializeVariation(unsigned char *buf, int variation) const
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


int AnalogInput::getSerializedLen(void) const
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


CtiPointDataMsg *AnalogInput::getPoint( const TimeCTO *cto ) const
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

    if( gDNPVerbose )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Analog input, value " << val << endl;
    }


    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, AnalogPointType);

    return tmpMsg;
}


//  ---  ANALOG INPUT FROZEN  ---

AnalogInputFrozen::AnalogInputFrozen(int variation) : AnalogInput(Group, variation),
    _tof(Time::TimeAndDate)
{

}


int AnalogInputFrozen::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    if( len < getSerializedLen() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _valid = false;
        pos = len;
    }
    else
    {
        switch(getVariation())
        {
            case AI16Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16Bit);
                break;
            }

            case AI16BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16BitNoFlag);
                break;
            }

            case AI16BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16Bit);
                pos += _tof.restore(buf + pos, len - pos);
                break;
            }

            case AI32Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI32Bit);
                break;
            }

            case AI32BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16BitNoFlag);
                break;
            }

            case AI32BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI32Bit);
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


int AnalogInputFrozen::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch(getVariation())
    {
        case AI16Bit:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16Bit);
            break;
        }

        case AI16BitNoFlag:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16BitNoFlag);
            break;
        }

        case AI16BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16Bit);
            pos += _tof.serialize(buf + pos);
            break;
        }

        case AI32Bit:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI32Bit);
            break;
        }

        case AI32BitNoFlag:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16BitNoFlag);
            break;
        }

        case AI32BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI32Bit);
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


int AnalogInputFrozen::getSerializedLen(void) const
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

AnalogInputChange::AnalogInputChange(int variation) : AnalogInput(Group, variation),
    _toc(Time::TimeAndDate)
{

}


int AnalogInputChange::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    if( len < getSerializedLen() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _valid = false;
        pos = len;
    }
    else
    {
        switch(getVariation())
        {
            case AI16BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16Bit);
                break;
            }

            case AI16BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI16Bit);
                pos += _toc.restore(buf + pos, len - pos);
                break;
            }

            case AI32BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI32Bit);
                break;
            }

            case AI32BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AnalogInput::AI32Bit);
                pos += _toc.restore(buf + pos, len - pos);
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


int AnalogInputChange::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch(getVariation())
    {
        case AI16BitNoTime:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16Bit);
            break;
        }

        case AI16BitWithTime:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI16Bit);
            pos += _toc.serialize(buf + pos);
            break;
        }

        case AI32BitNoTime:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI32Bit);
            break;
        }

        case AI32BitWithTime:
        {
            pos += serializeVariation(buf + pos, AnalogInput::AI32Bit);
            pos += _toc.serialize(buf + pos);
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


int AnalogInputChange::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case AI16BitNoTime:
        {
            retVal = 3;

            break;
        }
        case AI16BitWithTime:
        {
            retVal = 9;

            break;
        }
        case AI32BitNoTime:
        {
            retVal = 5;

            break;
        }
        case AI32BitWithTime:
        {
            retVal = 11;

            break;
        }
    }

    return retVal;
}


CtiPointDataMsg *AnalogInputChange::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    tmpMsg = AnalogInput::getPoint(cto);

    switch(getVariation())
    {
        case AI16BitWithTime:
        case AI32BitWithTime:
        {
            tmpMsg->setTime(_toc.getSeconds() + rwEpoch);
            tmpMsg->setMillis(_toc.getMilliseconds());

            break;
        }

        default:
        {
            break;
        }
    }

    return tmpMsg;
}


//  ---  ANALOG INPUT FROZEN EVENT  ---

AnalogInputFrozenEvent::AnalogInputFrozenEvent(int variation) : AnalogInput(Group, variation),
    _tofe(Time::TimeAndDate)
{

}


int AnalogInputFrozenEvent::restore(const unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int AnalogInputFrozenEvent::serialize(unsigned char *buf) const
{
    return 0;
}


int AnalogInputFrozenEvent::getSerializedLen(void) const
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

}
}
}

