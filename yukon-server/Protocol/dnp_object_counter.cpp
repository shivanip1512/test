#include "precompiled.h"

#include "dnp_object_counter.h"
#include "logger.h"

using std::endl;

namespace Cti {
namespace Protocols {
namespace DNP {

Counter::Counter(int variation) :
    Object(Group, variation),
    _counter(0)
{
    _flags.raw = 0;
    _flags.online = true;
}


Counter::Counter(int group, int variation) :
    Object(group, variation),
    _counter(0)
{
    _flags.raw = 0;
    _flags.online = true;
}


int Counter::restore(const unsigned char *buf, int len)
{
    //  ACH:  check minimum length, like the others
    return restoreVariation(buf, len, getVariation());
}


int Counter::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());;
}

unsigned long Counter::getValue() const
{
    return _counter;
}
unsigned char Counter::getFlags() const
{
    return _flags.raw;
}

void Counter::setValue(long value)
{
    _counter = value;
}

void Counter::setOnlineFlag(bool online)
{
    _flags.online = online;
}


int Counter::getSerializedLen(void) const
{
    int retVal = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case C_Binary32Bit:
        case C_Delta32Bit:
        {
            retVal = 5;
            break;
        }

        case C_Binary16Bit:
        case C_Delta16Bit:
        {
            retVal = 3;
            break;
        }
        case C_Binary32BitNoFlag:
        case C_Delta32BitNoFlag:
        {
            retVal = 4;
            break;
        }
        case C_Binary16BitNoFlag:
        case C_Delta16BitNoFlag:
        {
            retVal = 2;
            break;
        }
        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return retVal;
}


int Counter::restoreVariation(const unsigned char *buf, int len, int variation)
{
    int pos = 0;

    _valid = true;

    switch( variation )
    {
        case C_Binary32Bit:
        case C_Delta32Bit:
        {
            _flags.raw = buf[pos++];

            //  fall through
        }
        case C_Binary32BitNoFlag:
        case C_Delta32BitNoFlag:
        {
            unsigned char *val = (unsigned char *)&_counter;

            val[0] = buf[pos++];
            val[1] = buf[pos++];
            val[2] = buf[pos++];
            val[3] = buf[pos++];

            break;
        }

        case C_Binary16Bit:
        case C_Delta16Bit:
        {
            _flags.raw = buf[pos++];

            //  fall through
        }
        case C_Binary16BitNoFlag:
        case C_Delta16BitNoFlag:
        {
            unsigned char *val = (unsigned char *)&_counter;

            val[0] = buf[pos++];
            val[1] = buf[pos++];
            val[2] = 0;
            val[3] = 0;

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


int Counter::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;

    switch(variation)
    {
        case C_Binary32Bit:
        case C_Delta32Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case C_Binary32BitNoFlag:
        case C_Delta32BitNoFlag:
        {
            buf[pos++] =  _counter        & 0xff;
            buf[pos++] = (_counter >>  8) & 0xff;
            buf[pos++] = (_counter >> 16) & 0xff;
            buf[pos++] = (_counter >> 24) & 0xff;

            break;
        }

        case C_Binary16Bit:
        case C_Delta16Bit:
        {
            buf[pos++] = _flags.raw;
            //  fall through
        }
        case C_Binary16BitNoFlag:
        case C_Delta16BitNoFlag:
        {
            buf[pos++] =  _counter        & 0xff;
            buf[pos++] = (_counter >>  8) & 0xff;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }
    return pos;
}


CtiPointDataMsg *Counter::getPoint( const TimeCTO *cto ) const
{
    int quality = NormalQuality;

    if( ! _flags.online && gDNPOfflineNonUpdated )
    {
        quality = NonUpdatedQuality;
    }

    if( gDNPVerbose )
    {
        CTILOG_INFO(dout, "Counter object, value "<< _counter);
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    return new CtiPointDataMsg(0, _counter, quality, PulseAccumulatorPointType);
}



CounterEvent::CounterEvent(int variation) :
    Counter(Group, variation),
    _toc(Time::T_TimeAndDate)
{

}

int CounterEvent::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    switch(getVariation())
    {
        case CE_Binary32BitNoTime:
        case CE_Delta32BitNoTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32Bit);
            break;
        }
        case CE_Binary16BitNoTime:
        case CE_Delta16BitNoTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            break;
        }
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            pos += _toc.restore(buf + pos, len - pos);
            break;
        }
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            pos += _toc.restore(buf + pos, len - pos);
            break;
        }
    }

        //  ACH:  check minimum length, like the others
    return pos;
}


int CounterEvent::serialize(unsigned char *buf) const
{
    return serializeVariation(buf, getVariation());;
}

int CounterEvent::serializeVariation(unsigned char *buf, int variation) const
{
    int pos = 0;
    unsigned char flag = getFlags();
    unsigned long counter = getValue();

    switch(variation)
    {
        case CE_Binary32BitNoTime:
        case CE_Delta32BitNoTime:
        {
            buf[pos++] = flag;

            buf[pos++] =  counter        & 0xff;
            buf[pos++] = (counter >>  8) & 0xff;
            buf[pos++] = (counter >> 16) & 0xff;
            buf[pos++] = (counter >> 24) & 0xff;
            break;
        }
        case CE_Binary16BitNoTime:
        case CE_Delta16BitNoTime:
        {
            buf[pos++] = flag;
            buf[pos++] =  counter        & 0xff;
            buf[pos++] = (counter >>  8) & 0xff;
            break;
        }
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            buf[pos++] = flag;
            buf[pos++] =  counter        & 0xff;
            buf[pos++] = (counter >>  8) & 0xff;
            buf[pos++] = (counter >> 16) & 0xff;
            buf[pos++] = (counter >> 24) & 0xff;
            pos += _toc.serialize(buf + pos);
            break;
        }
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        {
            buf[pos++] = flag;
            buf[pos++] =  counter        & 0xff;
            buf[pos++] = (counter >>  8) & 0xff;
            pos += _toc.serialize(buf + pos);
           break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }
    return pos;
}


int CounterEvent::getSerializedLen(void) const
{
    int retVal = 0;

    const int variation = getVariation();

    switch( variation )
    {
        case CE_Binary32BitNoTime:
        case CE_Delta32BitNoTime:
        {
            retVal = 5;
            break;
        }
        case CE_Binary16BitNoTime:
        case CE_Delta16BitNoTime:
        {
            retVal = 3;
            break;
        }
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            retVal = 11;
            break;
        }
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        {
            retVal = 9;
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }


    return retVal;
}


CtiPointDataMsg *CounterEvent::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    //  inherited types just add on to the parent's values
    tmpMsg = Counter::getPoint(cto);

    switch( getVariation() )
    {
        case CE_Binary16BitWithTime:
        case CE_Delta16BitWithTime:
        case CE_Binary32BitWithTime:
        case CE_Delta32BitWithTime:
        {
            tmpMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
            tmpMsg->setTimeWithMillis(_toc.getSeconds(), _toc.getMilliseconds());

            break;
        }
    }

    if( gDNPVerbose )
    {
        CTILOG_INFO(dout, "Counter Event object, value "<< val);
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    //tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
}

void CounterEvent::setTime(CtiTime timestamp)
{
    _toc.setSeconds(timestamp.seconds());
}



CounterFrozen::CounterFrozen(int variation) : Counter(Group, variation)
{

}

int CounterFrozen::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    switch(getVariation())
    {
        /*
        CF_Delta32Bit                  =  3,
        CF_Delta16Bit                  =  4,
        CF_Binary32BitWithTimeOfFreeze =  5,
        CF_Binary16BitWithTimeOfFreeze =  6,
        CF_Delta32BitWithTimeOfFreeze  =  7,
        CF_Delta16BitWithTimeOfFreeze  =  8,
        CF_Delta32BitNoFlag            = 11,
        CF_Delta16BitNoFlag            = 12,
        */

        case CF_Binary32Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32Bit);
            break;
        }
        case CF_Binary32BitNoFlag:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary32BitNoFlag);
            break;
        }
        case CF_Binary16Bit:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16Bit);
            break;
        }
        case CF_Binary16BitNoFlag:
        {
            pos += restoreVariation(buf + pos, len - pos, C_Binary16BitNoFlag);
            break;
        }
    }

        //  ACH:  check minimum length, like the others
    return pos;
}


int CounterFrozen::serialize(unsigned char *buf) const
{
    return 0;
}


int CounterFrozen::getSerializedLen(void) const
{
    CTILOG_ERROR(dout, "function unimplemented");

    return 0;
}


CtiPointDataMsg *CounterFrozen::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0;
    int quality;

    //  inherited types just add on to the parent's values
    tmpMsg = Counter::getPoint(cto);

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

    if( gDNPVerbose && tmpMsg )
    {
        CTILOG_INFO(dout, "Frozen counter object, value "<< tmpMsg->getValue());
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    //tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, PulseAccumulatorPointType);

    return tmpMsg;
}


CounterFrozenEvent::CounterFrozenEvent(int variation) : Object(Group, variation)
{

}

}
}
}

