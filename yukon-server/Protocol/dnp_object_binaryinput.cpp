/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryinput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2008/04/03 15:25:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "dnp_object_binaryinput.h"
#include "logger.h"

using std::endl;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

BinaryInput::BinaryInput(int group, int variation) : Object(group, variation)
{
    _bi.raw = 0;
}


BinaryInput::BinaryInput(int variation) : Object(Group, variation)
{
    _bi.raw = 0;
}


int BinaryInput::restore(const unsigned char *buf, int len)
{
    return restoreVariation(buf, len, getVariation());
}


int BinaryInput::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch( variation )
    {
        case BI_WithStatus:
        {
            _bi.raw = buf[pos++];

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int BinaryInput::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    _valid = true;

    bitpos = bitoffset;

    const int variation = getVariation();

    switch(variation)
    {
        case BI_SingleBitPacked:
        {
            _bi.flags.online = true;
            _bi.flags.state = (buf[bitpos/8] >> (bitpos++)) & 0x01;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");

            _valid = false;
            bitpos = len * 8;
        }
    }

    return bitpos - bitoffset;
}


int BinaryInput::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    // TODO: was this a mistake?
    switch(variation)
    {
        case BI_WithStatus:
        {
            buf[pos++] = _bi.raw;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int BinaryInput::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());
}


void BinaryInput::setStateValue(long value)
{
    _bi.flags.state = value;
}

void BinaryInput::setOnlineFlag(bool online)
{
    _bi.flags.online = online;
}

int BinaryInput::getSerializedLen(void) const
{
    int retVal = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BI_WithStatus:
        {
            retVal = 1;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return retVal;
}


CtiPointDataMsg *BinaryInput::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val;
    int quality = NormalQuality;

    val = _bi.flags.state;

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
    if (!_bi.flags.online && gDNPOfflineNonUpdated)
    {
        quality = NonUpdatedQuality;
    }

    if( gDNPVerbose )
    {
        CTILOG_INFO(dout, "Binary input, value "<< val);
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, quality, StatusPointType);

    return tmpMsg;
}



BinaryInputChange::BinaryInputChange(int variation) : BinaryInput(variation),
    _time(Time::T_TimeAndDate),
    _timeRelative(TimeDelay::TD_Fine)
{

}


int BinaryInputChange::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case BIC_WithoutTime:
        {
            pos += restoreVariation(buf + pos, len - pos, BinaryInput::BI_WithStatus);

            break;
        }

        case BIC_WithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, BinaryInput::BI_WithStatus);
            pos += _time.restore(buf + pos, len - pos);

            break;
        }

        case BIC_WithRelativeTime:
        {
            pos += restoreVariation(buf + pos, len - pos, BinaryInput::BI_WithStatus);
            pos += _timeRelative.restore(buf + pos, len - pos);

            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout, "Time Relative: "<< _timeRelative.getSeconds() <<"s, "<< _timeRelative.getMilliseconds() <<"ms");
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");

            _valid = false;
            pos = len;
        }
    }

    return pos;
}


int BinaryInputChange::serialize(unsigned char *buf) const
{
    int pos = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BIC_WithoutTime:
        {
            pos += serializeVariation(buf + pos, BI_WithStatus);

            break;
        }

        case BIC_WithTime:
        {
            pos += serializeVariation(buf + pos, BI_WithStatus);
            pos += _time.serialize(buf + pos);

            break;
        }

        case BIC_WithRelativeTime:
        {
            pos += serializeVariation(buf + pos, BI_WithStatus);
            pos += _timeRelative.serialize(buf + pos);

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int BinaryInputChange::getSerializedLen(void) const
{
    int len = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BIC_WithoutTime:
        {
            len = 1;

            break;
        }

        case BIC_WithTime:
        {
            len = 7;

            break;
        }

        case BIC_WithRelativeTime:
        {
            len = 3;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
            len = 0;
        }
    }

    return len;
}


CtiPointDataMsg *BinaryInputChange::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg = NULL;

    tmpMsg = BinaryInput::getPoint(cto);

    const int variation = getVariation();

    switch(variation)
    {
        case BIC_WithoutTime:
        {
            break;
        }

        case BIC_WithTime:
        {
            tmpMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
            tmpMsg->setTimeWithMillis(_time.getSeconds(), _time.getMilliseconds());

            break;
        }

        case BIC_WithRelativeTime:
        {
            unsigned long seconds;
            unsigned milliseconds;

            milliseconds = cto->getMilliseconds() + _timeRelative.getMilliseconds();
            seconds      = cto->getSeconds()      + _timeRelative.getSeconds();

            seconds      += milliseconds / 1000;
            milliseconds %= 1000;

            tmpMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
            tmpMsg->setTimeWithMillis(seconds, milliseconds);

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return tmpMsg;
}


void BinaryInputChange::setTime(CtiTime timestamp)
{
    _time.setSeconds(timestamp.seconds());
}


}
}
}


