/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryinput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_binaryinput.h"
#include "logger.h"


CtiDNPBinaryInput::CtiDNPBinaryInput(int group, int variation) : CtiDNPObject(group, variation)
{
    _bi.raw = 0;
}


CtiDNPBinaryInput::CtiDNPBinaryInput(int variation) : CtiDNPObject(Group, variation)
{
    _bi.raw = 0;
}


int CtiDNPBinaryInput::restore(unsigned char *buf, int len)
{
    return restoreVariation(buf, len, getVariation());
}


int CtiDNPBinaryInput::restoreVariation(unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch( variation )
    {
        case WithStatus:
        {
            _bi.raw = buf[pos++];

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


int CtiDNPBinaryInput::restoreBits(unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    _valid = true;

    bitpos = bitoffset;

    switch( getVariation() )
    {
        case SingleBitPacked:
        {
            _bi.flags.state = (buf[bitpos/8] >> (bitpos++)) & 0x01;

            break;
        }

        default:
        {
            {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            bitpos = len * 8;

            break;
        }
    }

    return bitpos - bitoffset;
}


int CtiDNPBinaryInput::serializeVariation(unsigned char *buf, int variation)
{
    int pos = 0;

    switch(getVariation())
    {
        case WithStatus:
        {
            buf[pos++] = _bi.raw;

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


int CtiDNPBinaryInput::serialize(unsigned char *buf)
{
    return serializeVariation(buf, getVariation());
}


int CtiDNPBinaryInput::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case WithStatus:
        {
            retVal = 1;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retVal = 0;
        }
    }

    return retVal;
}


CtiPointDataMsg *CtiDNPBinaryInput::getPoint( const CtiDNPTimeCTO *cto )
{
    CtiPointDataMsg *tmpMsg;

    double val;
    int quality;

    val = _bi.flags.state;

    //  this has to be rethought in terms of being called by inherited classes with ENTIRELY DIFFERENT VARIATIONS
    /*switch(getVariation())
    {
        case WithStatus:
        {
            //  fall through
        }
        case SingleBitPacked:
        {
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
    }*/

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
        dout << "Binary input, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, StatusPointType);

    return tmpMsg;
}



CtiDNPBinaryInputChange::CtiDNPBinaryInputChange(int variation) : CtiDNPBinaryInput(variation),
    _time(CtiDNPTime::TimeAndDate),
    _timeRelative(CtiDNPTimeDelay::Fine)
{

}


int CtiDNPBinaryInputChange::restore(unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
    {
        case WithoutTime:
        {
            pos += restoreVariation(buf + pos, len - pos, CtiDNPBinaryInput::WithStatus);

            break;
        }

        case WithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, CtiDNPBinaryInput::WithStatus);
            pos += _time.restore(buf + pos, len - pos);

            break;
        }

        case WithRelativeTime:
        {
            pos += restoreVariation(buf + pos, len - pos, CtiDNPBinaryInput::WithStatus);
            pos += _timeRelative.restore(buf + pos, len - pos);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Time Relative: " << _timeRelative.getSeconds() << "s, " << _timeRelative.getMilliseconds() << "ms" << endl;
            }

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


int CtiDNPBinaryInputChange::serialize(unsigned char *buf)
{
    int pos = 0;

    switch( getVariation() )
    {
        case WithoutTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPBinaryInput::WithStatus);

            break;
        }

        case WithTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPBinaryInput::WithStatus);
            pos += _time.serialize(buf + pos);

            break;
        }

        case WithRelativeTime:
        {
            pos += serializeVariation(buf + pos, CtiDNPBinaryInput::WithStatus);
            pos += _timeRelative.serialize(buf + pos);

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


int CtiDNPBinaryInputChange::getSerializedLen(void)
{
    int len = 0;

    switch( getVariation() )
    {
        case WithoutTime:
        {
            len = 1;

            break;
        }

        case WithTime:
        {
            len = 7;

            break;
        }

        case WithRelativeTime:
        {
            len = 3;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            len = 0;
        }
    }

    return len;
}


CtiPointDataMsg *CtiDNPBinaryInputChange::getPoint( const CtiDNPTimeCTO *cto )
{
    CtiPointDataMsg *tmpMsg = NULL;

    switch(getVariation())
    {
        case WithTime:
        {
            tmpMsg = CtiDNPBinaryInput::getPoint(cto);

            tmpMsg->setTime(_time.getSeconds() + rwEpoch);
            tmpMsg->setMillis(_time.getMilliseconds());

            break;
        }

        case WithRelativeTime:
        {
            unsigned long seconds;
            unsigned milliseconds;

            milliseconds = cto->getMilliseconds() + _timeRelative.getMilliseconds();
            seconds      = cto->getSeconds()      + _timeRelative.getSeconds();

            seconds      += milliseconds / 1000;
            milliseconds %= 1000;

            tmpMsg = CtiDNPBinaryInput::getPoint(cto);

            tmpMsg->setTime(seconds + rwEpoch);
            tmpMsg->setMillis(milliseconds);
        }

        default:
        {
            break;
        }
    }

    return tmpMsg;
}

