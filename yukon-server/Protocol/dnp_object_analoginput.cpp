/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2006/01/24 20:08:18 $
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AI_32Bit:
        {
            _flags.raw = buf[pos++];
            //  fall through
        }
        case AI_32BitNoFlag:
        {
            _value = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _value |= buf[pos++] ;
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AI_16Bit:
        {
            _flags.raw = buf[pos++];
            //  fall through
        }
        case AI_16BitNoFlag:
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AI_32Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case AI_32BitNoFlag:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >> 24) & 0xff;

            break;
        }

        case AI_16Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case AI_16BitNoFlag:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AI_32Bit:
        {
            retVal = 5;
            break;
        }

        case AI_16Bit:
        {
            retVal = 3;
            break;
        }

        case AI_32BitNoFlag:
        {
            retVal = 4;
            break;
        }

        case AI_16BitNoFlag:
        {
            retVal = 2;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AI_32Bit:
        case AI_16Bit:
        case AI_32BitNoFlag:
        case AI_16BitNoFlag:
        {
            val = _value;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Analog input, value " << val << endl;
    }


    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, AnalogPointType);

    return tmpMsg;
}


//  ---  ANALOG INPUT FROZEN  ---

AnalogInputFrozen::AnalogInputFrozen(int variation) : AnalogInput(Group, variation),
    _tof(Time::T_TimeAndDate)
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _valid = false;
        pos = len;
    }
    else
    {
        switch(getVariation())
        {
            case AIF_16Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16Bit);
                break;
            }

            case AIF_16BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16BitNoFlag);
                break;
            }

            case AIF_16BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16Bit);
                pos += _tof.restore(buf + pos, len - pos);
                break;
            }

            case AIF_32Bit:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_32Bit);
                break;
            }

            case AIF_32BitNoFlag:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16BitNoFlag);
                break;
            }

            case AIF_32BitWithTimeOfFreeze:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_32Bit);
                pos += _tof.restore(buf + pos, len - pos);
                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AIF_16Bit:
        {
            pos += serializeVariation(buf + pos, AI_16Bit);
            break;
        }

        case AIF_16BitNoFlag:
        {
            pos += serializeVariation(buf + pos, AI_16BitNoFlag);
            break;
        }

        case AIF_16BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, AI_16Bit);
            pos += _tof.serialize(buf + pos);
            break;
        }

        case AIF_32Bit:
        {
            pos += serializeVariation(buf + pos, AI_32Bit);
            break;
        }

        case AIF_32BitNoFlag:
        {
            pos += serializeVariation(buf + pos, AI_16BitNoFlag);
            break;
        }

        case AIF_32BitWithTimeOfFreeze:
        {
            pos += serializeVariation(buf + pos, AI_32Bit);
            pos += _tof.serialize(buf + pos);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AIF_16Bit:
        {
            retVal = 3;
            break;
        }

        case AIF_16BitNoFlag:
        {
            retVal = 2;
            break;
        }

        case AIF_16BitWithTimeOfFreeze:
        {
            retVal = 9;
            break;
        }

        case AIF_32Bit:
        {
            retVal = 5;
            break;
        }

        case AIF_32BitNoFlag:
        {
            retVal = 4;
            break;
        }

        case AIF_32BitWithTimeOfFreeze:
        {
            retVal = 11;
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;
            break;
        }
    }

    return retVal;
}


//  ---  ANALOG INPUT CHANGE  ---

AnalogInputChange::AnalogInputChange(int variation) : AnalogInput(Group, variation),
    _toc(Time::T_TimeAndDate)
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _valid = false;
        pos = len;
    }
    else
    {
        switch(getVariation())
        {
            case AIC_16BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16Bit);
                break;
            }

            case AIC_16BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_16Bit);
                pos += _toc.restore(buf + pos, len - pos);
                break;
            }

            case AIC_32BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_32Bit);
                break;
            }

            case AIC_32BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_32Bit);
                pos += _toc.restore(buf + pos, len - pos);
                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AIC_16BitNoTime:
        {
            pos += serializeVariation(buf + pos, AI_16Bit);
            break;
        }

        case AIC_16BitWithTime:
        {
            pos += serializeVariation(buf + pos, AI_16Bit);
            pos += _toc.serialize(buf + pos);
            break;
        }

        case AIC_32BitNoTime:
        {
            pos += serializeVariation(buf + pos, AI_32Bit);
            break;
        }

        case AIC_32BitWithTime:
        {
            pos += serializeVariation(buf + pos, AI_32Bit);
            pos += _toc.serialize(buf + pos);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case AIC_16BitNoTime:
        {
            retVal = 3;

            break;
        }
        case AIC_16BitWithTime:
        {
            retVal = 9;

            break;
        }
        case AIC_32BitNoTime:
        {
            retVal = 5;

            break;
        }
        case AIC_32BitWithTime:
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
        case AIC_16BitWithTime:
        case AIC_32BitWithTime:
        {
            tmpMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
            tmpMsg->setTime(_toc.getSeconds());
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
    _tofe(Time::T_TimeAndDate)
{

}


int AnalogInputFrozenEvent::restore(const unsigned char *buf, int len)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int AnalogInputFrozenEvent::serialize(unsigned char *buf) const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return 0;
}


int AnalogInputFrozenEvent::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case AIFE_16BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AIFE_16BitWithTime:
        {
            retVal = 0;

            break;
        }
        case AIFE_32BitNoTime:
        {
            retVal = 0;

            break;
        }
        case AIFE_32BitWithTime:
        {
            retVal = 0;

            break;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retVal;
}

}
}
}

