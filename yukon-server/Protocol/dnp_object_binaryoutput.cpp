/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryoutput
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


#include "dnp_object_binaryoutput.h"
#include "logger.h"

using std::endl;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

BinaryOutput::BinaryOutput(int variation) : Object(Group, variation)
{
    _bo.raw = 0;
}

int BinaryOutput::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
    {
        case BO_WithStatus:
        {
            _bo.raw = buf[pos++];

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


int BinaryOutput::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    switch( getVariation() )
    {
        case BO_SingleBit:
        {
            _bo.flags.state = (buf[bitpos/8] >> (bitpos++)) & 0x01;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            bitpos = len * 8;

            break;
        }
    }

    return bitpos - bitoffset;
}


int BinaryOutput::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch(getVariation())
    {
        case BO_WithStatus:
        {
            buf[pos++] = _bo.raw;

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


int BinaryOutput::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case BO_WithStatus:
        {
            retVal = 1;
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


CtiPointDataMsg *BinaryOutput::getPoint( const TimeCTO *cto ) const
{
    CtiPointDataMsg *tmpMsg;

    double val = 0.0;
    int quality;

    switch(getVariation())
    {
        case BO_WithStatus:
        case BO_SingleBit:
        {
            val = _bo.flags.state;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Binary output, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, StatusPointType);

    return tmpMsg;
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


int BinaryOutputControl::restore(const unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
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


int BinaryOutputControl::restoreBits(const unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    switch( getVariation() )
    {
        case BOC_PatternMask:
        {
            _patternMask = (buf[bitpos/8] >> bitpos) & 0x01;

            bitpos++;

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _valid = false;
            bitpos = len * 8;

            break;
        }
    }

    return bitpos - bitoffset;
}


int BinaryOutputControl::serialize(unsigned char *buf) const
{
    int pos = 0;

    switch(getVariation())
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    return pos;
}


int BinaryOutputControl::getSerializedLen(void) const
{
    int retVal;

    switch(getVariation())
    {
        case BOC_ControlRelayOutputBlock:
        case BOC_PatternControlBlock:
        {
            retVal = ProtocolSize;
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


int BinaryOutputControl::getStatus( void ) const
{
    int retVal;

    retVal = _crob_or_pcb.block.status;

    return retVal;
}

}
}
}

