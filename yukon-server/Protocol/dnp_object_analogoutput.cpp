/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analogoutput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/03/10 21:24:52 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_analogoutput.h"
#include "logger.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

AnalogOutput::AnalogOutput(int group, int variation) : Object(group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


AnalogOutput::AnalogOutput(int variation) : Object(Group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


void AnalogOutput::setValue(long value)
{
    _value = value;
}


int AnalogOutput::restore(const unsigned char *buf, int len)
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
        pos += restoreVariation(buf + pos, len - pos, getVariation());
    }

    return pos;
}


int AnalogOutput::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AO32Bit:
        {
            _flags.raw = buf[pos++];

            _value = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _value |= buf[pos++];
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AO16Bit:
        {
            short tmpValue;

            _flags.raw = buf[pos++];

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
        case AO32Bit:
        {
            buf[pos++] = _flags.raw;

            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >> 24) & 0xff;

            break;
        }

        case AO16Bit:
        {
            buf[pos++] = _flags.raw;

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

            break;
        }
    }

    return pos;
}


int AnalogOutput::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case AO32Bit:
        {
            retVal = 5;
            break;
        }
        case AO16Bit:
        {
            retVal = 3;
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


CtiPointDataMsg *AnalogOutput::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val;
    int quality;

    switch(getVariation())
    {
        case AO32Bit:
        {
            val = _value;
            break;
        }

        case AO16Bit:
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
        dout << "Analog output, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, AnalogPointType);

    return tmpMsg;
}


AnalogOutputBlock::AnalogOutputBlock(int variation) : Object(Group, variation)
{
    _value  = 0;
    _status = 0;
}


int AnalogOutputBlock::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch(getVariation())
    {
        case AOB32Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, AnalogOutput::AO32Bit);

            break;
        }

        case AOB16Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, AnalogOutput::AO16Bit);

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int AnalogOutputBlock::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch(variation)
    {
        case AOB32Bit:
        {
            _status = buf[pos++];

            _value = 0;

            //  these 32 bits will fill up the long, including the sign bit
            _value |= buf[pos++];
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AOB16Bit:
        {
            short tmpValue;

            _status = buf[pos++];

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

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int AnalogOutputBlock::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch(getVariation())
    {
        case AOB32Bit:
        {
            pos += serializeVariation(buf, AnalogOutputBlock::AOB32Bit);

            break;
        }

        case AOB16Bit:
        {
            pos += serializeVariation(buf, AnalogOutputBlock::AOB16Bit);

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


int AnalogOutputBlock::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    switch(variation)
    {
        case AOB32Bit:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >> 24) & 0xff;

            buf[pos++] = _status;

            break;
        }

        case AOB16Bit:
        {
            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;

            buf[pos++] = _status;

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


int AnalogOutputBlock::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case AOB32Bit:
        {
            retVal = 5;
            break;
        }
        case AOB16Bit:
        {
            retVal = 3;
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


void AnalogOutputBlock::setControl(long value)
{
    _value = value;
}

}
}
}


