#include "precompiled.h"

#include "dnp_object_analoginput.h"
#include "logger.h"

#include <boost/cstdint.hpp>


using std::endl;
using boost::int16_t;
using boost::int32_t;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

//  ---  ANALOG INPUT  ---

AnalogInput::AnalogInput(int group, int variation) : Object(group, variation)
{
    _doubleValue = 0.0;
    _longValue = 0;
    _flags.raw = 0;
}


AnalogInput::AnalogInput(int variation) : Object(Group, variation)
{
    _doubleValue = 0.0;
    _longValue = 0;
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
            _longValue = restoreValue<int32_t>(buf + pos);

            pos += 4;

            break;
        }

        case AI_16Bit:
        {
            _flags.raw = buf[pos++];
            //  fall through
        }
        case AI_16BitNoFlag:
        {
            _longValue = restoreValue<int16_t>(buf + pos);

            pos += 2;

            break;
        }

        case AI_SingleFloat:
        {
            _flags.raw = buf[pos++];

            _doubleValue = restoreValue<float>(buf + pos);

            pos += 4;

            break;
        }
        case AI_DoubleFloat:
        {
            _flags.raw = buf[pos++];

            _doubleValue = restoreValue<double>(buf + pos);

            pos += 8;

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
            serializeValue<int32_t>(buf + pos, _longValue);

            pos += 4;

            break;
        }

        case AI_16Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case AI_16BitNoFlag:
        {
            serializeValue<int16_t>(buf + pos, _longValue);

            pos += 4;

            break;
        }

        case AI_SingleFloat:
        {
            buf[pos++] = _flags.raw;

            serializeValue<float>(buf + pos, _doubleValue);

            pos += 4;

            break;
        }
        case AI_DoubleFloat:
        {
            buf[pos++] = _flags.raw;

            serializeValue<double>(buf + pos, _doubleValue);

            pos += 8;

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

void AnalogInput::setValue(double value)
{
    _longValue   = value;
    _doubleValue = value;
}
void AnalogInput::setOnlineFlag(bool online)
{
    _flags.online = online;
}

int AnalogInput::getSerializedLen(void) const
{
    switch( getVariation() )
    {
        case AI_32Bit:          return 5;
        case AI_16Bit:          return 3;

        case AI_32BitNoFlag:    return 4;
        case AI_16BitNoFlag:    return 2;

        case AI_SingleFloat:     return 5;
        case AI_DoubleFloat:     return 9;

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


CtiPointDataMsg *AnalogInput::getPoint( const TimeCTO *cto ) const
{
    double val = 0.0;
    int quality = NormalQuality;

    switch(getVariation())
    {
        case AI_32Bit:
        case AI_16Bit:
        {
            if (!_flags.online && gDNPOfflineNonUpdated)
            {
                quality = NonUpdatedQuality;
            }
        }
        case AI_32BitNoFlag:
        case AI_16BitNoFlag:
        {
            val = _longValue;
            break;
        }

        case AI_SingleFloat:
        case AI_DoubleFloat:
        {
            if (!_flags.online && gDNPOfflineNonUpdated)
            {
                quality = NonUpdatedQuality;
            }

            val = _doubleValue;
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

    if( gDNPVerbose )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Analog input, value " << val << endl;
    }


    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    return new CtiPointDataMsg(0, val, quality, AnalogPointType);
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

            case AIC_SingleFloatNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_SingleFloat);
                break;
            }

            case AIC_SingleFloatWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_SingleFloat);
                pos += _toc.restore(buf + pos, len - pos);
                break;
            }

            case AIC_DoubleFloatNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_DoubleFloat);
                break;
            }

            case AIC_DoubleFloatWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, AI_DoubleFloat);
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

        case AIC_SingleFloatNoTime:
        {
            pos += serializeVariation(buf + pos, AI_SingleFloat);
            break;
        }

        case AIC_SingleFloatWithTime:
        {
            pos += serializeVariation(buf + pos, AI_SingleFloat);
            pos += _toc.serialize(buf + pos);
            break;
        }

        case AIC_DoubleFloatNoTime:
        {
            pos += serializeVariation(buf + pos, AI_DoubleFloat);
            break;
        }

        case AIC_DoubleFloatWithTime:
        {
            pos += serializeVariation(buf + pos, AI_DoubleFloat);
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
    switch(getVariation())
    {
        case AIC_16BitNoTime:   return 3;
        case AIC_16BitWithTime: return 9;
        case AIC_32BitNoTime:   return 5;
        case AIC_32BitWithTime: return 11;

        case AIC_SingleFloatNoTime:     return 5;
        case AIC_SingleFloatWithTime:   return 11;
        case AIC_DoubleFloatNoTime:     return 9;
        case AIC_DoubleFloatWithTime:   return 15;
    }

    return 0;
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

void AnalogInputChange::setTime(CtiTime timestamp)
{
    _toc.setSeconds(timestamp.seconds());
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
    int retVal = 0;

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

