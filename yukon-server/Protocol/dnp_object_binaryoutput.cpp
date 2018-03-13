#include "precompiled.h"


#include "dnp_object_binaryoutput.h"
#include "logger.h"

using std::endl;

namespace Cti {
namespace Protocols {
namespace DNP {

BinaryOutput::BinaryOutput(int variation) : Object(Group, variation)
{
    _bo.raw = 0;
}

int BinaryOutput::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case BO_WithStatus:
        {
            _bo.raw = buf[pos++];

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


int BinaryOutput::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case BO_SingleBit:
        {
            _bo.flags.online = true;
            _bo.flags.state = (buf[bitpos/8] >> (bitpos++)) & 0x01;

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


int BinaryOutput::serialize(unsigned char *buf) const
{
    int pos = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BO_WithStatus:
        {
            buf[pos++] = _bo.raw;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int BinaryOutput::getSerializedLen(void) const
{
    int retVal = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BO_WithStatus:
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


void BinaryOutput::setStateValue(bool value)
{
    _bo.flags.state = value;
}


void BinaryOutput::setOnlineFlag(bool online)
{
    _bo.flags.online = online;
}


CtiPointDataMsg *BinaryOutput::getPoint( const TimeCTO *cto ) const
{
    double val = 0.0;
    int quality = NormalQuality;

    const int variation = getVariation();

    switch(variation)
    {
        case BO_WithStatus:
        case BO_SingleBit:
        {
            val = _bo.flags.state;
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    if( ! _bo.flags.online && gDNPOfflineNonUpdated )
    {
        quality = NonUpdatedQuality;
    }

    if( gDNPVerbose )
    {
        CTILOG_INFO(dout, "Binary output, value "<< val);
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    return new CtiPointDataMsg(0, val, quality, StatusPointType);
}



BinaryOutputControl::BinaryOutputControl(int variation) :
    Object(Group, variation),
    _patternMask(false)
{
    memset( _crob_or_pcb.raw, 0, ProtocolSize );
}


void BinaryOutputControl::setControlBlock(unsigned long onTime, unsigned long offTime,
                                                unsigned char count, ControlCode code, bool queue, bool clear, TripClose tripclose)
{
    _crob_or_pcb.block.on_time  = onTime;
    _crob_or_pcb.block.off_time = offTime;
    _crob_or_pcb.block.count    = count;

    _crob_or_pcb.block.control_code.code       = code;
    _crob_or_pcb.block.control_code.clear      = clear;
    _crob_or_pcb.block.control_code.queue      = queue;
    _crob_or_pcb.block.control_code.trip_close = tripclose;

    _crob_or_pcb.block.status = 0;
}


void BinaryOutputControl::setStatus(ControlStatus s)
{
    _crob_or_pcb.block.status = static_cast<unsigned char>(s);
}


int BinaryOutputControl::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case BOC_ControlRelayOutputBlock:
        case BOC_PatternControlBlock:
        {
            for( int i = 0; i < ProtocolSize; i++ )
            {
                _crob_or_pcb.raw[i] = buf[pos++];
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


int BinaryOutputControl::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    const int variation = getVariation();

    switch(variation)
    {
        case BOC_PatternMask:
        {
            _patternMask = (buf[bitpos/8] >> bitpos) & 0x01;

            bitpos++;

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


int BinaryOutputControl::serialize(unsigned char *buf) const
{
    int pos = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BOC_ControlRelayOutputBlock:
        case BOC_PatternControlBlock:
        {
            for( int i = 0; i < ProtocolSize; i++ )
            {
                buf[pos++] = _crob_or_pcb.raw[i];
            }

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return pos;
}


int BinaryOutputControl::getSerializedLen(void) const
{
    int retVal = 0;

    const int variation = getVariation();

    switch(variation)
    {
        case BOC_ControlRelayOutputBlock:
        case BOC_PatternControlBlock:
        {
            retVal = ProtocolSize;
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return retVal;
}


unsigned char BinaryOutputControl::getStatus( void ) const
{
    return _crob_or_pcb.block.status;
}

auto BinaryOutputControl::getControlCode() const -> ControlCode
{
    return static_cast<ControlCode>(_crob_or_pcb.block.control_code.code);
}

auto BinaryOutputControl::getTripClose() const -> TripClose
{
    return static_cast<TripClose>(_crob_or_pcb.block.control_code.trip_close);
}

unsigned long BinaryOutputControl::getOnTime() const
{
    return _crob_or_pcb.block.on_time;
}

unsigned long BinaryOutputControl::getOffTime() const
{
    return _crob_or_pcb.block.off_time;
}

unsigned char BinaryOutputControl::getCount() const
{
    return _crob_or_pcb.block.count;
}

bool BinaryOutputControl::getQueue() const
{
    return _crob_or_pcb.block.control_code.queue;
}

bool BinaryOutputControl::getClear() const
{
    return _crob_or_pcb.block.control_code.clear;
}

}
}
}

