#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analogoutput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/01/07 21:19:36 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_object_analogoutput.h"
#include "logger.h"

CtiDNPAnalogOutput::CtiDNPAnalogOutput(int group, int variation) : CtiDNPObject(group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


CtiDNPAnalogOutput::CtiDNPAnalogOutput(int variation) : CtiDNPObject(Group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


void CtiDNPAnalogOutput::setValue(long value)
{
    _value = value;
}


int CtiDNPAnalogOutput::restore(unsigned char *buf, int len)
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


int CtiDNPAnalogOutput::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AO32Bit:
        {
            _flags.raw = buf[pos++];

            if( buf[pos] & 0x80 )
            {
                _value = -1;
            }
            else
            {
                _value = 0;
            }

            _value |= buf[pos++];
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AO16Bit:
        {
            _flags.raw = buf[pos++];

            if( buf[pos] & 0x80 )
            {
                _value = -1;
            }
            else
            {
                _value = 0;
            }

            _value |= buf[pos++];
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


int CtiDNPAnalogOutput::serialize(unsigned char *buf)
{
    return serializeVariation(buf, getVariation());
}


int CtiDNPAnalogOutput::serializeVariation(unsigned char *buf, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AO32Bit:
        {
            _flags.raw = buf[pos++];

            buf[pos++] =  _value        & 0xff;
            buf[pos++] = (_value >>  8) & 0xff;
            buf[pos++] = (_value >> 16) & 0xff;
            buf[pos++] = (_value >> 24) & 0xff;

            break;
        }

        case AO16Bit:
        {
            _flags.raw = buf[pos++];

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


int CtiDNPAnalogOutput::getSerializedLen(void)
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


CtiPointDataMsg *CtiDNPAnalogOutput::getPoint( void )
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


CtiDNPAnalogOutputBlock::CtiDNPAnalogOutputBlock(int variation) : CtiDNPObject(Group, variation)
{
    _value  = 0;
    _status = 0;
}


int CtiDNPAnalogOutputBlock::restore(unsigned char *buf, int len)
{
    int pos = 0;

    switch(getVariation())
    {
        case AOB32Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogOutput::AO32Bit);

            break;
        }

        case AOB16Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogOutput::AO16Bit);

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


int CtiDNPAnalogOutputBlock::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    switch(variation)
    {
        case AOB32Bit:
        {
            _status = buf[pos++];

            if( buf[pos] & 0x80 )
            {
                _value = -1;
            }

            _value  = buf[pos++];
            _value |= buf[pos++] <<  8;
            _value |= buf[pos++] << 16;
            _value |= buf[pos++] << 24;

            break;
        }

        case AOB16Bit:
        {
            _status = buf[pos++];

            if( buf[pos] & 0x80 )
            {
                _value = -1;
            }

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


int CtiDNPAnalogOutputBlock::serialize(unsigned char *buf)
{
    int pos = 0;

    switch(getVariation())
    {
        case AOB32Bit:
        {
            pos += serializeVariation(buf, CtiDNPAnalogOutputBlock::AOB32Bit);

            break;
        }

        case AOB16Bit:
        {
            pos += serializeVariation(buf, CtiDNPAnalogOutputBlock::AOB16Bit);

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


int CtiDNPAnalogOutputBlock::serializeVariation(unsigned char *buf, int variation)
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


int CtiDNPAnalogOutputBlock::getSerializedLen(void)
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


void CtiDNPAnalogOutputBlock::setControl(long value)
{
    _value = value;
}
