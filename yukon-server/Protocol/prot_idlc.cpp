/*-----------------------------------------------------------------------------*
*
* File:   prot_idlc
*
* Date:   2006-sep-28
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/10/05 16:44:51 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_idlc.h"

#include "cti_asmc.h"

namespace Cti       {
namespace Protocol  {

IDLC::IDLC()
{
}

IDLC::IDLC(const IDLC &aRef)
{
    *this = aRef;
}

IDLC::~IDLC()   {}

IDLC &IDLC::operator=(const IDLC &aRef)
{
    if( this != &aRef )
    {
        _address  = aRef._address;
    }

    return *this;
}


void IDLC::setAddress( unsigned short address )
{
    _address = address;
}


void IDLC::sendFrame( CtiXfer &xfer )
{
    unsigned short crc;

    //  byte 0
    _out_frame.header.flag      = FramingFlag;

    //  byte 1
    _out_frame.header.address   = _address;
    _out_frame.header.direction = 1;

    //  byte 2
    _out_frame.header.control.unsequenced = 0;
    _out_frame.header.control.final_frame = 1;
    _out_frame.header.control.request_sequence  = _master_sequence;
    _out_frame.header.control.response_sequence = _slave_sequence;

    _out_frame.data[0] = _out_payload_length;
    memcpy(_out_frame.data + 1, _out_payload, _out_payload_length);

    //  the CRC omits the framing byte
    crc = NCrcCalc_C(_out_frame.raw + 1, _out_payload_length + 3);

    //  MSB first
    _out_frame.data[_out_payload_length + 4] = (crc >> 8) & 0xff;
    _out_frame.data[_out_payload_length + 5] = (crc >> 0) & 0xff;

    xfer.setOutCount(_out_payload_length + Frame_DataPacket_OverheadLength);
    xfer.setOutBuffer(_out_frame.raw);

    xfer.setInCountExpected(0);
    xfer.setInCountActual(&_in_actual);
    xfer.setInBuffer(0);
}


void IDLC::sendRetransmit( CtiXfer &xfer )
{
    unsigned short crc;

    //  byte 0
    _out_frame.header.flag      = FramingFlag;

    //  byte 1
    _out_frame.header.address   = _address;
    _out_frame.header.direction = 1;

    //  this sequence needs to have the upper (response) bits filled in with the
    //    sequence number you expect
    //  byte 2
    _out_frame.header.control.code = Control_RetransmitRequest;
    _out_frame.header.control.response_sequence = _slave_sequence;

    //  the CRC omits the framing byte
    crc = NCrcCalc_C(_out_frame.raw + 1, 2);

    //  MSB first
    _out_frame.data[0] = (crc >> 8) & 0xff;
    _out_frame.data[1] = (crc >> 0) & 0xff;

    xfer.setOutCount(Frame_ControlPacketLength);
    xfer.setOutBuffer(_out_frame.raw);

    xfer.setInCountExpected(0);
    xfer.setInBuffer(0);
    xfer.setInCountActual(&_in_actual);
}


void IDLC::sendReset( CtiXfer &xfer )
{
    unsigned short crc;

    //  byte 0
    _out_frame.header.flag      = FramingFlag;

    //  byte 1
    _out_frame.header.address   = _address;
    _out_frame.header.direction = 1;

    //  byte 2
    _out_frame.header.control.code = Control_ResetRequest;

    //  the CRC omits the framing byte
    crc = NCrcCalc_C(_out_frame.raw + 1, 2);

    //  MSB first
    _out_frame.data[0] = (crc >> 8) & 0xff;
    _out_frame.data[1] = (crc >> 0) & 0xff;

    xfer.setOutCount(Frame_ControlPacketLength);
    xfer.setOutBuffer(_out_frame.raw);

    xfer.setInCountExpected(0);
    xfer.setInBuffer(0);
    xfer.setInCountActual(&_in_actual);
}


void IDLC::recvFrame( CtiXfer &xfer, unsigned bytes_received )
{
    //  we will be aligned on a framing byte when decode() calls alignFrame(),
    //    so this header information will be good
    if( bytes_received < Frame_MinimumLength )
    {
        xfer.setInCountExpected(Frame_MinimumLength - bytes_received);
    }
    else
    {
        xfer.setInCountExpected(calcFrameLength(_in_frame) - bytes_received);
    }

    xfer.setOutCount(0);
    xfer.setOutBuffer(0);

    xfer.setInCountExpected(Frame_MinimumLength);
    xfer.setInBuffer(_in_frame.raw + bytes_received);
    xfer.setInCountActual(&_in_actual);
}


unsigned IDLC::calcFrameLength(const frame &f)
{
    unsigned retval = 0;

    if( isControlFrame(f) )
    {
        retval = Frame_MinimumLength;
    }
    else
    {
        retval = f.data[0];
    }

    return retval;
}


unsigned IDLC::alignFrame(CtiXfer &xfer, frame &f, unsigned long &bytes_received)
{
    unsigned unaligned = 0;

    while( bytes_received && f.raw[unaligned] != FramingFlag )
    {
        unaligned++;
        bytes_received--;
    }

    if( unaligned && bytes_received )
    {
        memmove(f.raw, f.raw + unaligned, bytes_received);
    }

    return unaligned;
}


bool IDLC::isCRCValid(const frame &f)
{
    bool crc_valid = false;

    unsigned length = calcFrameLength(f);
    unsigned short crc;

    //  CRC doesn't include the framing byte or the two CRC bytes at the end
    crc = NCrcCalc_C(f.raw + 1, length - 3);

    if( ((unsigned char *)&crc)[0] == f.raw[length - 2] &&
        ((unsigned char *)&crc)[1] == f.raw[length - 1] )
    {
        crc_valid = true;
    }

    return crc_valid;
}


bool IDLC::isControlFrame(const frame &f)
{
    bool retval = false;

    if( f.header.control.code          == Control_ResetAcknowlegde  ||
        f.header.control.code          == Control_ResetRequest      ||
        (f.header.control.code & 0x1f) == Control_RejectWithRestart ||
        (f.header.control.code & 0x1f) == Control_RetransmitRequest )
    {
        retval = true;
    }

    return retval;
}


bool IDLC::isCompleteFrame(const frame &f, unsigned bytes_received)
{
    return calcFrameLength(f) <= bytes_received;
}


int IDLC::generate( CtiXfer &xfer )
{
    int retval = NoError;

    switch( _io_state )
    {
        case State_IO_Output:       sendFrame(xfer);            break;

        case State_IO_Reset:        sendReset(xfer);            break;

        case State_IO_Retransmit:   sendRetransmit(xfer);       break;

        case State_IO_Input:        recvFrame(xfer, _in_recv);  break;

        case State_IO_Complete:
        case State_IO_Invalid:
        case State_IO_Failed:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled state (" << _io_state << ") in  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retval = BADRANGE;
        }
    }

    return retval;
}


int IDLC::decode( CtiXfer &xfer, int status )
{
    int retval = NoError;

    //  if there's an error, stay on the same state
    if( status )
    {
        _comm_errors++;
    }
    else
    {
        switch( _io_state )
        {
            case State_IO_Input:
            {
                if( !_in_recv )
                {
                    //  if we haven't received anything yet, make sure that we align on a
                    //    framing byte first...
                    //  in the normal case, this should return 0, meaning that we're aligned
                    //    and that the whole _in_actual count is valid
                    _framing_seek_length += alignFrame(xfer, _in_frame, _in_actual);

                    _in_recv += _in_actual;
                }
                else if( !isCompleteFrame(_in_frame, _in_recv) )
                {
                    //  we didn't get the whole thing yet - loop back through generate() so we can grab the remainder
                    ++_sanity_counter;
                }
                else if( !isCRCValid(_in_frame) )
                {
                    //  bad CRC, re-request frame
                    _io_state = State_IO_Retransmit;
                    _protocol_errors++;
                }
                else if( _in_frame.header.direction )
                {
                    //  nothing coming from the slave should have the direction bit set
                    _protocol_errors++;
                }
                else if( isControlFrame(_in_frame) )
                {
                    if( _in_frame.header.control.code == Control_ResetAcknowlegde )
                    {
                        _master_sequence = 1;
                        _slave_sequence  = 1;
                    }
                    else if( (_in_frame.header.control.code & 0x1f) == Control_RejectWithRestart )
                    {
                        _master_sequence = _in_frame.header.control.response_sequence;
                        _protocol_errors++;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - unhandled control code (" << _in_frame.header.control.code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        _protocol_errors++;
                    }
                }
                else if( _in_frame.header.control.response_sequence != _slave_sequence )
                {
                    _io_state = State_IO_Retransmit;
                    _protocol_errors++;
                }
                else
                {
                    //  successful response - increment our sequence numbers
                    _master_sequence = (_master_sequence + 1) % 8;
                    _slave_sequence  = (_slave_sequence  + 1) % 8;

                    //  and copy the data
                    _in_payload_length = _in_frame.data[0];

                    memcpy(_in_payload, _in_frame.data + 1, _in_payload_length);

                    //  we're done
                    _io_state = State_IO_Complete;
                }

                break;
            }

            case State_IO_Output:
            case State_IO_Retransmit:
            {
                _io_state = State_IO_Input;

                break;
            }

            case State_IO_Complete:
            case State_IO_Failed:
            case State_IO_Invalid:
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unhandled state (" << _io_state << ") in  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _protocol_errors++;
                _io_state = State_IO_Failed;

                break;
            }
        }
    }

    if( _sanity_counter      > Insanity              ||
        _protocol_errors     > MaximumProtocolErrors ||
        _comm_errors         > MaximumCommErrors     ||
        _framing_seek_length > MaximumFramingSeekLength )
    {
        //  this should be a nicer error - this is very generic
        retval = !NORMAL;
        _io_state = State_IO_Failed;
    }

    return retval;
}


bool IDLC::isTransactionComplete( void )
{
    bool retval = false;

    switch( _io_state )
    {
        case State_IO_Input:
        case State_IO_Output:
        case State_IO_Retransmit:
            break;

        //  note the "default" case there - we exit instead of looping forever
        case State_IO_Complete:
        case State_IO_Failed:
        case State_IO_Invalid:
        default:
            retval = true;
    }

    return retval;
}


bool IDLC::errorCondition( void )
{
    return _io_state == State_IO_Failed;
}


bool IDLC::setOutPayload( const unsigned char *payload, unsigned len )
{
    if( payload && len && len < Frame_MaximumPayloadLength )
    {
        _io_state = State_IO_Output;

        memcpy(_out_payload, payload, len);

        _out_payload_length = len;

        //  this is the only place these are reset to 0
        _sanity_counter  = 0;
        _comm_errors     = 0;
        _protocol_errors = 0;
    }
    else
    {
        _io_state = State_IO_Invalid;
        _out_payload_length = 0;
    }

    return _out_payload_length > 0;
}


void IDLC::getInPayload( unsigned char *buf )
{
    if( buf )
    {
        memcpy(buf, _in_payload, _in_payload_length);
    }
}


int IDLC::getInPayloadLength( void )
{
    return _in_payload_length;
}


}
}

