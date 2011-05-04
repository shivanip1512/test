/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analogoutput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2006/01/24 20:08:18 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_analogoutput.h"
#include "logger.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

AnalogOutputStatus::AnalogOutputStatus(int group, int variation) : Object(group, variation)
{
    _longValue = 0;
    _doubleValue = 0.0;
    _flags.raw = 0;
}


AnalogOutputStatus::AnalogOutputStatus(int variation) : Object(Group, variation)
{
    _longValue = 0;
    _doubleValue = 0.0;
    _flags.raw = 0;
}


int AnalogOutputStatus::restore(const unsigned char *buf, int len)
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
        pos += restoreVariation(buf + pos, len - pos, getVariation());
    }

    return pos;
}


int AnalogOutputStatus::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AOS_32Bit:
        {
            _flags.raw = buf[pos++];

            _longValue = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _longValue |= buf[pos++];
            _longValue |= buf[pos++] <<  8;
            _longValue |= buf[pos++] << 16;
            _longValue |= buf[pos++] << 24;

            break;
        }

        case AOS_16Bit:
        {
            short tmpValue;

            _flags.raw = buf[pos++];

            tmpValue = 0;

            //  these 16 bits will fill up the short, including the sign bit...
            tmpValue |= buf[pos++];
            tmpValue |= buf[pos++] << 8;

            //  ...  so we can use the compiler's cast to convert it over
            _longValue = tmpValue;

            break;
        }

        case AOS_SingleFloat:
        {
            _flags.raw = buf[pos++];

            _doubleValue = *(reinterpret_cast<const float *>(buf + pos));

            pos += 4;

            break;
        }

        case AOS_DoubleFloat:
        {
            _flags.raw = buf[pos++];

            _doubleValue = *(reinterpret_cast<const double *>(buf + pos));

            pos += 8;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int AnalogOutputStatus::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());
}


int AnalogOutputStatus::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    switch(variation)
    {
        case AOS_32Bit:
        {
            buf[pos++] = _flags.raw;

            buf[pos++] =  _longValue        & 0xff;
            buf[pos++] = (_longValue >>  8) & 0xff;
            buf[pos++] = (_longValue >> 16) & 0xff;
            buf[pos++] = (_longValue >> 24) & 0xff;

            break;
        }

        case AOS_16Bit:
        {
            buf[pos++] = _flags.raw;

            buf[pos++] =  _longValue        & 0xff;
            buf[pos++] = (_longValue >>  8) & 0xff;

            break;
        }

        case AOS_SingleFloat:
        {
            buf[pos++] = _flags.raw;

            *(reinterpret_cast<float *>(buf + pos)) = _doubleValue;

            pos += 4;

            break;
        }

        case AOS_DoubleFloat:
        {
            buf[pos++] = _flags.raw;

            *(reinterpret_cast<double *>(buf + pos)) = _doubleValue;

            pos += 8;

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


int AnalogOutputStatus::getSerializedLen(void) const
{
    switch(getVariation())
    {
        case AOS_32Bit:  return 5;
        case AOS_16Bit:  return 3;

        case AOS_SingleFloat:  return 5;
        case AOS_DoubleFloat:  return 9;

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


CtiPointDataMsg *AnalogOutputStatus::getPoint( const TimeCTO *cto ) const
{
    double val = 0.0;
    int quality;

    switch(getVariation())
    {
        case AOS_32Bit:
        case AOS_16Bit:
        {
            val = _longValue;
            break;
        }

        case AOS_SingleFloat:
        case AOS_DoubleFloat:
        {
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
        dout << "Analog output, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    return new CtiPointDataMsg(0, val, NormalQuality, AnalogPointType);
}


AnalogOutput::AnalogOutput(int variation) : Object(Group, variation)
{
    _longValue = 0;
    _doubleValue = 0.0;
    _status = 0;
}


int AnalogOutput::restore(const unsigned char *buf, int len)
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
        pos += restoreVariation(buf + pos, len - pos, getVariation());
    }

    return pos;
}


int AnalogOutput::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch(variation)
    {
        case AO_32Bit:
        {
            _longValue = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _longValue |= buf[pos++];
            _longValue |= buf[pos++] <<  8;
            _longValue |= buf[pos++] << 16;
            _longValue |= buf[pos++] << 24;

            _status = buf[pos++];

            break;
        }

        case AO_16Bit:
        {
            short tmpValue = 0;

            //  these 16 bits bytes will fill up the short, including the sign bit...
            tmpValue |= buf[pos++];
            tmpValue |= buf[pos++] << 8;

            //  ...  so we can use the compiler's cast to convert it over
            _longValue = tmpValue;

            _status = buf[pos++];

            break;
        }

        case AO_SingleFloat:
        {
            _doubleValue = *(reinterpret_cast<const float *>(buf + pos));

            pos += 4;

            _status = buf[pos++];

            break;
        }

        case AO_DoubleFloat:
        {
            _doubleValue = *(reinterpret_cast<const double *>(buf + pos));

            pos += 8;

            _status = buf[pos++];

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int AnalogOutput::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());
}


int AnalogOutput::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    switch(variation)
    {
        case AO_32Bit:
        {
            buf[pos++] =  _longValue        & 0xff;
            buf[pos++] = (_longValue >>  8) & 0xff;
            buf[pos++] = (_longValue >> 16) & 0xff;
            buf[pos++] = (_longValue >> 24) & 0xff;

            buf[pos++] = _status;

            break;
        }

        case AO_16Bit:
        {
            buf[pos++] =  _longValue        & 0xff;
            buf[pos++] = (_longValue >>  8) & 0xff;

            buf[pos++] = _status;

            break;
        }

        case AO_SingleFloat:
        {
            *(reinterpret_cast<float *>(buf + pos)) = _doubleValue;

            pos += 4;

            buf[pos++] = _status;

            break;
        }

        case AO_DoubleFloat:
        {
            *(reinterpret_cast<double *>(buf + pos)) = _doubleValue;

            pos += 8;

            buf[pos++] = _status;

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


int AnalogOutput::getSerializedLen(void) const
{
    switch(getVariation())
    {
        case AO_32Bit:  return 5;
        case AO_16Bit:  return 3;

        case AO_SingleFloat:    return 5;
        case AO_DoubleFloat:    return 9;

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


unsigned char AnalogOutput::getStatus() const
{
    return _status;
}


double AnalogOutput::getValue() const
{
    switch(getVariation())
    {
        case AO_32Bit:
        case AO_16Bit:
        {
            return _longValue;
        }

        case AO_SingleFloat:
        case AO_DoubleFloat:
        {
            return _doubleValue;
        }

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return 0;
}


void AnalogOutput::setControl(double value)
{
    _doubleValue = value;
    _longValue   = value;
}

}
}
}


