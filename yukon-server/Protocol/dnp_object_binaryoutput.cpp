/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryoutput
*
* Date:   7/8/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dnp_object_binaryoutput.h"
#include "logger.h"

CtiDNPBinaryOutput::CtiDNPBinaryOutput(int variation) : CtiDNPObject(Group, variation)
{

}

int CtiDNPBinaryOutput::restore(unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
    {
        case WithStatus:
        {
            _bo.raw = buf[pos++];

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


int CtiDNPBinaryOutput::restoreBits(unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    switch( getVariation() )
    {
        case SingleBit:
        {
            _bo.flags.state = (buf[bitpos/8] >> (bitpos++)) & 0x01;

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


int CtiDNPBinaryOutput::serialize(unsigned char *buf)
{
    int pos = 0;

    switch(getVariation())
    {
        case WithStatus:
        {
            buf[pos++] = _bo.raw;

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


int CtiDNPBinaryOutput::getSerializedLen(void)
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
            break;
        }
    }

    return retVal;
}


CtiPointDataMsg *CtiDNPBinaryOutput::getPoint( const CtiDNPTimeCTO *cto )
{
    CtiPointDataMsg *tmpMsg;

    double val;
    int quality;

    switch(getVariation())
    {
        case WithStatus:
        {
            //  fall through
        }
        case SingleBit:
        {
            val = _bo.flags.state;
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
        dout << "Binary output, value " << val << endl;
    }

    //  the ID will be replaced by the offset by the object block, which will then be used by the
    //    device to figure out the true ID
    tmpMsg = CTIDBG_new CtiPointDataMsg(0, val, NormalQuality, StatusPointType);

    return tmpMsg;
}



CtiDNPBinaryOutputControl::CtiDNPBinaryOutputControl(int variation) : CtiDNPObject(Group, variation)
{

}


void CtiDNPBinaryOutputControl::setControlBlock(unsigned long onTime, unsigned long offTime,
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


int CtiDNPBinaryOutputControl::restore(unsigned char *buf, int len)
{
    int pos = 0;

    _valid = true;

    switch( getVariation() )
    {
        case ControlRelayOutputBlock:
        case PatternControlBlock:
        {
            for( int i = 0; i < 11; i++ )
            {
                _crob_or_pcb.raw[i] = buf[pos++];
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


int CtiDNPBinaryOutputControl::restoreBits(unsigned char *buf, int bitoffset, int len)
{
    int bitpos;

    bitpos = bitoffset;
    _valid = true;

    switch( getVariation() )
    {
        case PatternMask:
        {
            _patternMask = (buf[bitpos/8] >> bitpos) & 0x01;

            bitpos++;

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


int CtiDNPBinaryOutputControl::serialize(unsigned char *buf)
{
    int pos = 0;

    switch(getVariation())
    {
        case ControlRelayOutputBlock:
        case PatternControlBlock:
        {
            for( int i = 0; i < 11; i++ )
            {
                buf[pos++] = _crob_or_pcb.raw[i];
            }

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


int CtiDNPBinaryOutputControl::getSerializedLen(void)
{
    int retVal;

    switch(getVariation())
    {
        case ControlRelayOutputBlock:
        case PatternControlBlock:
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


int CtiDNPBinaryOutputControl::getStatus( void ) const
{
    int retVal;

    retVal = _crob_or_pcb.block.status;

    return retVal;
}

