#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_analoginput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/01/07 21:19:36 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dnp_object_analoginput.h"
#include "logger.h"


//  ---  ANALOG INPUT  ---

CtiDNPAnalogInput::CtiDNPAnalogInput(int group, int variation) : CtiDNPObject(group, variation)
{
    _value = 0;
    _flags.raw = 0;
}


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
            if( buf[pos] & 0x80 )
            {
                _value = -1;
            }
            else
            {
                _value = 0;
            }

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


CtiPointDataMsg *CtiDNPAnalogInput::getPoint( void )
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

CtiDNPAnalogInputFrozen::CtiDNPAnalogInputFrozen(int variation) : CtiDNPAnalogInput(Group, variation),
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

CtiDNPAnalogInputChange::CtiDNPAnalogInputChange(int variation) : CtiDNPAnalogInput(Group, variation),
    _toc(CtiDNPTime::TimeAndDate)
{

}


int CtiDNPAnalogInputChange::restore(unsigned char *buf, int len)
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
            case AI16BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16Bit);
                break;
            }

            case AI16BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI16Bit);
                pos += _toc.restore(buf + pos, len - pos);
                break;
            }

            case AI32BitNoTime:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI32Bit);
                break;
            }

            case AI32BitWithTime:
            {
                pos += restoreVariation(buf + pos, len - pos, CtiDNPAnalogInput::AI32Bit);
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


int CtiDNPAnalogInputChange::serialize(unsigned char *buf)
{
    int pos = 0;

    switch(getVariation())
    {
        case AI16BitNoTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16Bit);
            break;
        }

        case AI16BitWithTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI16Bit);
            pos += _toc.serialize(buf + pos);
            break;
        }

        case AI32BitNoTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI32Bit);
            break;
        }

        case AI32BitWithTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPAnalogInput::AI32Bit);
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


int CtiDNPAnalogInputChange::getSerializedLen(void)
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


CtiPointDataMsg *CtiDNPAnalogInputChange::getPoint( void )
{
    CtiPointDataMsg *tmpMsg;

    tmpMsg = CtiDNPAnalogInput::getPoint();

    switch(getVariation())
    {
        case AI16BitWithTime:
        case AI32BitWithTime:
        {
            tmpMsg->setTime(_toc.getSeconds());

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

CtiDNPAnalogInputFrozenEvent::CtiDNPAnalogInputFrozenEvent(int variation) : CtiDNPAnalogInput(Group, variation),
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

