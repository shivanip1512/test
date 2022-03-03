#include "precompiled.h"

#include "dnp_object_analogoutput.h"
#include "logger.h"
#include "std_helper.h"

#include <boost/cstdint.hpp>

using std::endl;

using boost::int16_t;
using boost::int32_t;

namespace Cti {
namespace Protocols {
namespace DNP {

AnalogOutputStatus::AnalogOutputStatus(int group, int variation) : Object(group, variation)
{
    _value = 0.0;
    _flags.raw = 0;
    _flags.online = true;
}


AnalogOutputStatus::AnalogOutputStatus(int variation) : Object(Group, variation)
{
    _value = 0.0;
    _flags.raw = 0;
    _flags.online = true;
}


int AnalogOutputStatus::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int serializedLen = getSerializedLen();

    if( len < serializedLen )
    {
        CTILOG_ERROR(dout, "len < serializedLen ("<< len <<" < "<< serializedLen <<")");

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

            _value = restoreValue<int32_t>(buf + pos);

            pos += 4;

            break;
        }

        case AOS_16Bit:
        {
            _flags.raw = buf[pos++];

            _value = restoreValue<int16_t>(buf + pos);

            pos += 2;

            break;
        }

        case AOS_SingleFloat:
        {
            _flags.raw = buf[pos++];

            _value = restoreValue<float>(buf + pos);

            pos += 4;

            break;
        }

        case AOS_DoubleFloat:
        {
            _flags.raw = buf[pos++];

            _value = restoreValue<double>(buf + pos);

            pos += 8;

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

            serializeValue<int32_t>(buf + pos, _value);

            pos += 4;

            break;
        }

        case AOS_16Bit:
        {
            buf[pos++] = _flags.raw;

            serializeValue<int16_t>(buf + pos, _value);

            pos += 2;

            break;
        }

        case AOS_SingleFloat:
        {
            buf[pos++] = _flags.raw;

            serializeValue<float>(buf + pos, _value);

            pos += 4;

            break;
        }

        case AOS_DoubleFloat:
        {
            buf[pos++] = _flags.raw;

            serializeValue<double>(buf + pos, _value);

            pos += 8;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int AnalogOutputStatus::getSerializedLen(void) const
{
    const int variation = getVariation();

    switch(variation)
    {
        case AOS_32Bit:  return 5;
        case AOS_16Bit:  return 3;

        case AOS_SingleFloat:  return 5;
        case AOS_DoubleFloat:  return 9;

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return 0;
}


void AnalogOutputStatus::setValue(double value)
{
    _value = value;
}


void AnalogOutputStatus::setOnlineFlag(bool online)
{
    _flags.online = online;
}


CtiPointDataMsg *AnalogOutputStatus::getPoint( const TimeCTO *cto ) const
{
    double val = 0.0;
    int quality = NormalQuality;
    const int variation = getVariation();

    switch(variation)
    {
        case AOS_32Bit:
        case AOS_16Bit:
        case AOS_SingleFloat:
        case AOS_DoubleFloat:
        {
            val = _value;
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    if( ! _flags.online && gDNPOfflineNonUpdated )
    {
        quality = NonUpdatedQuality;
    }

    if( gDNPVerbose )
    {
        CTILOG_INFO(dout, "Analog output, value "<< val);
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    return new CtiPointDataMsg(0, val, quality, AnalogPointType);
}


AnalogOutput::AnalogOutput(int variation) : Object(Group, variation)
{
    _value = 0.0;
    _status = 0;
}


int AnalogOutput::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int serializedLen = getSerializedLen();

    if( len < serializedLen )
    {
        CTILOG_ERROR(dout, "len < serializedLen ("<< len <<" < "<< serializedLen <<")");

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
            _value = restoreValue<int32_t>(buf + pos);

            pos += 4;

            _status = buf[pos++];

            break;
        }

        case AO_16Bit:
        {
            _value = restoreValue<int16_t>(buf + pos);

            pos += 2;

            _status = buf[pos++];

            break;
        }

        case AO_SingleFloat:
        {
            _value = restoreValue<float>(buf + pos);

            pos += 4;

            _status = buf[pos++];

            break;
        }

        case AO_DoubleFloat:
        {
            _value = restoreValue<double>(buf + pos);

            pos += 8;

            _status = buf[pos++];

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
            serializeValue<int32_t>(buf + pos, _value);

            pos += 4;

            buf[pos++] = _status;

            break;
        }

        case AO_16Bit:
        {
            serializeValue<int16_t>(buf + pos, _value);

            pos += 2;

            buf[pos++] = _status;

            break;
        }

        case AO_SingleFloat:
        {
            serializeValue<float>(buf + pos, _value);

            pos += 4;

            buf[pos++] = _status;

            break;
        }

        case AO_DoubleFloat:
        {
            serializeValue<double>(buf + pos, _value);

            pos += 8;

            buf[pos++] = _status;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int AnalogOutput::getSerializedLen(void) const
{
    const int variation = getVariation();

    switch(variation)
    {
        case AO_32Bit:  return 5;
        case AO_16Bit:  return 3;

        case AO_SingleFloat:    return 5;
        case AO_DoubleFloat:    return 9;

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
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
    const int variation = getVariation();

    switch(getVariation())
    {
        case AO_32Bit:
        case AO_16Bit:
        case AO_SingleFloat:
        case AO_DoubleFloat:
        {
            return _value;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return 0;
}


void AnalogOutput::setControl(double value)
{
    _value   = value;
}


void AnalogOutput::setStatus(ControlStatus status)
{
    _status = as_underlying(status);
}

}
}
}


