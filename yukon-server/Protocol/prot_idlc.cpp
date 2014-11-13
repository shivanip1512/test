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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/09/19 11:40:41 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <limits>
using namespace std;

#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_idlc.h"

#include "cti_asmc.h"

namespace Cti {
namespace Protocols {

IDLC::IDLC() :
    _address(0),
    _io_operation(IO_Operation_Invalid),
    _control_state(Control_State_OK),
    _slave_sequence (numeric_limits<unsigned char>::max()),  //  these are what trigger the IDLC reset
    _master_sequence(numeric_limits<unsigned char>::max()),
    _out_sent(0),
    _in_data_length(0),
    _in_expected(0),
    _in_actual(0),
    _in_recv(0),
    _framing_seek_length(0),
    _comm_errors(0),
    _protocol_errors(0),
    _input_loops(0)
{
    memset( &_out_frame, 0, sizeof(frame) );
    memset( &_in_frame,  0, sizeof(frame) );
}

IDLC::~IDLC()   {}

void IDLC::setAddress( unsigned short address )
{
    _address = address;
}


void IDLC::sendFrame( CtiXfer &xfer )
{
    unsigned short crc;

    unsigned char out_frame_length  = _out_data.size() + Frame_DataPacket_OverheadLength;

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

    _out_frame.data[0] = _out_data.size();

    std::copy(_out_data.begin(), _out_data.end(), _out_frame.data + 1);

    //  the CRC omits the framing byte
    crc = NCrcCalc_C(_out_frame.raw + 1, out_frame_length - 1 - 2);  //  minus 1 for the framing byte and 2 for the CRC

    //  MSB first
    _out_frame.raw[out_frame_length - 2] = (crc >> 8) & 0xff;
    _out_frame.raw[out_frame_length - 1] = (crc >> 0) & 0xff;

    xfer.setOutCount(out_frame_length);
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
    _out_frame.header.control.code = ControlCode_RetransmitRequest;
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
    _out_frame.header.control.code = ControlCode_ResetRequest;

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
        retval = f.data[0] + Frame_DataPacket_OverheadLength;
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

    if( ((crc >> 8) & 0xff) == f.raw[length - 2] &&  //  MSB
        ( crc       & 0xff) == f.raw[length - 1] )   //  LSB
    {
        crc_valid = true;
    }

    return crc_valid;
}


bool IDLC::isControlFrame(const frame &f)
{
    bool retval = false;

    if( f.header.control.code          == ControlCode_ResetAcknowledge  ||
        f.header.control.code          == ControlCode_ResetRequest      ||
        (f.header.control.code & 0x1f) == ControlCode_RejectWithRestart ||
        (f.header.control.code & 0x1f) == ControlCode_RetransmitRequest )
    {
        retval = true;
    }

    return retval;
}


bool IDLC::isCompleteFrame(const frame &f, unsigned bytes_received)
{
    return calcFrameLength(f) <= bytes_received;
}


YukonError_t IDLC::generate( CtiXfer &xfer )
{
    YukonError_t retval = ClientErrors::None;

    if( control_pending() )
    {
        retval = generate_control(xfer);
    }
    else
    {
        switch( _io_operation )
        {
            case IO_Operation_Output:   sendFrame(xfer);            break;

            case IO_Operation_Input:    recvFrame(xfer, _in_recv);  break;

            default:
            {
                CTILOG_ERROR(dout, "unhandled state ("<< _io_operation <<")");

                retval = ClientErrors::BadRange;
            }
        }
    }

    return retval;
}


YukonError_t IDLC::generate_control( CtiXfer &xfer )
{
    switch( _control_state )
    {
        case Control_State_ResetSend:   sendReset(xfer);            break;
        case Control_State_ResetRecv:   recvFrame(xfer, _in_recv);  break;

        case Control_State_Retransmit:  sendRetransmit(xfer);   break;

        case Control_State_OK:
        default:
            //  this should loop around with no problems
            break;
    }

    return ClientErrors::None;
}


YukonError_t IDLC::decode( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retval = ClientErrors::None;
    _in_data.clear();

    if( control_pending() )
    {
        retval = decode_control(xfer, status);
    }
    else if( status )
    {
        _comm_errors++;
    }
    else
    {
        switch( _io_operation )
        {
            case IO_Operation_Input:
            {
                if( process_inbound(xfer, status) )
                {
                    if( isControlFrame(_in_frame) )
                    {
                        if( retval = process_control(_in_frame) )
                        {
                            _protocol_errors++;
                        }
                        else
                        {
                            recv();  //  reset us for the next inbound
                        }
                    }
                    else if( _in_frame.header.control.request_sequence != _slave_sequence )
                    {
                        //_io_state = IO_State_Retransmit;
                        _protocol_errors++;
                    }
                    else
                    {
                        //  successful response - increment our sequence numbers
                        _master_sequence = _in_frame.header.control.response_sequence;
                        _slave_sequence  = (_slave_sequence + 1) % 8;

                        //  and copy the data
                        _in_data.assign(_in_frame.data + 1,
                                        _in_frame.data + 1 + _in_frame.data[0]);

                        //  we're done
                        _io_operation  = IO_Operation_Complete;
                    }

                }

                break;
            }

            case IO_Operation_Output:
            {
                _io_operation  = IO_Operation_Complete;

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unhandled operation ("<< _io_operation <<")");

                _protocol_errors++;
                _io_operation  = IO_Operation_Failed;

                break;
            }
        }
    }

    if( _input_loops         >  MaximumInputLoops     ||
        _protocol_errors     >= MaximumProtocolErrors ||
        _comm_errors         >= MaximumCommErrors     ||
        _framing_seek_length >= MaximumFramingSeekLength )
    {
        _io_operation  = IO_Operation_Failed;

        if ( _framing_seek_length >= MaximumFramingSeekLength )
        {
            retval = ClientErrors::Framing;
        }
        else if ( _input_loops > MaximumInputLoops )
        {
            retval = ClientErrors::ReadTimeout;
        }
        else if ( status )
        {
            retval = status;
        }
        else
        {
            retval = ClientErrors::Abnormal;
        }
    }

    return retval;
}


YukonError_t IDLC::decode_control( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retval = ClientErrors::None;

    switch( _control_state )
    {
        case Control_State_ResetSend:
        {
            _control_state = Control_State_ResetRecv;
            break;
        }
        case Control_State_ResetRecv:
        {
            if( process_inbound(xfer, status) )
            {
                if( isControlFrame(_in_frame) )
                {
                    retval = process_control(_in_frame);
                }
                else
                {
                    CTILOG_ERROR(dout, "unhandled frame ["<< CtiNumStr(_in_frame.header.control.code).hex(2) <<"]");

                    _protocol_errors++;

                    _control_state = Control_State_OK;
                }
            }

            break;
        }

        case Control_State_Retransmit:
        {
            _control_state = Control_State_OK;
            break;
        }

        case Control_State_OK:
        default:
            //  this should loop around with no problems
            break;
    }

    return retval;
}

YukonError_t IDLC::process_control( frame in_frame )
{
    YukonError_t retval = ClientErrors::None;

    if( in_frame.header.control.code == ControlCode_ResetAcknowledge )
    {
        _master_sequence = 0;
        _slave_sequence  = 0;

        _control_state = Control_State_OK;
    }
    else if( (in_frame.header.control.code & 0x1f) == ControlCode_RejectWithRestart )
    {
        // The end device is unhappy with us, we submit our new request with
        // the slave and master it asked for.
        _master_sequence = in_frame.header.control.code >> 5;
        _slave_sequence  = _master_sequence;
        _protocol_errors++;

        _control_state = Control_State_OK;
    }
/*    else if( (in_frame.header.control.code & 0x1f) == ControlCode_RetransmitRequest )
    {

    }*/
    else
    {
        CTILOG_ERROR(dout, "unhandled control frame ["<< CtiNumStr(in_frame.header.control.code).hex(2) <<"]");

        _protocol_errors++;

        _control_state = Control_State_OK;
    }

    return retval;
}


bool IDLC::process_inbound( CtiXfer &xfer, YukonError_t status )
{
    bool valid_frame = false;

    if( status )
    {
        _comm_errors++;
    }
    else
    {
        if( !_in_recv )
        {
            //  if we haven't received anything yet, make sure that we align on a
            //    framing byte first...
            //  in the normal case, this should return 0, meaning that we're aligned
            //    and that the whole _in_actual count is valid
            _framing_seek_length += alignFrame(xfer, _in_frame, _in_actual);
        }

        _in_recv += _in_actual;

        if( !isCompleteFrame(_in_frame, _in_recv) )
        {
            //  we didn't get the whole thing yet - loop back through generate() so we can grab the remainder
            ++_input_loops;
        }
        else if( !isCRCValid(_in_frame) )
        {
            //  bad CRC, re-request frame
            //_IO_Operation = IO_Operation_Retransmit;
            _protocol_errors++;
        }
        else if( _in_frame.header.direction )
        {
            //  nothing coming from the slave should have the direction bit set
            _protocol_errors++;
        }
        else
        {
            valid_frame = true;
        }
    }

    return valid_frame;
}


bool IDLC::control_pending( void )
{
    if( _control_state == Control_State_OK )
    {
        //  uninitialized
        if( _slave_sequence > 7 || _master_sequence > 7 )
        {
            _control_state = Control_State_ResetSend;
        }
    }

    return _control_state != Control_State_OK;
}


bool IDLC::isTransactionComplete( void ) const
{
    //  if there's an error OR we're not inputting or outputting
    return errorCondition() || !(_io_operation == IO_Operation_Input ||
                                 _io_operation == IO_Operation_Output);
}


bool IDLC::errorCondition( void ) const
{
    return _io_operation == IO_Operation_Failed;
}


bool IDLC::send( const std::vector<unsigned char> &buf )
{
    return setOutData(buf);
}


bool IDLC::recv( void )
{
    _io_operation  = IO_Operation_Input;
    _control_state = Control_State_OK;
    _in_recv  = 0;
    _framing_seek_length = 0;

    return true;
}


bool IDLC::init( void )
{
    _io_operation = IO_Operation_Invalid;

    return true;
}


bool IDLC::setOutData( const std::vector<unsigned char> &buf )
{
    _out_data.clear();

    if( !buf.empty() && buf.size() < Frame_MaximumDataLength )
    {
        _io_operation  = IO_Operation_Output;
        _control_state = Control_State_OK;

        _out_data.assign(buf.begin(), buf.end());

        //  this is the only place these are reset to 0
        _input_loops     = 0;
        _comm_errors     = 0;
        _protocol_errors = 0;

        _in_recv  = 0;
        _framing_seek_length = 0;
    }
    else
    {
        _io_operation    = IO_Operation_Invalid;
        _control_state   = Control_State_OK;
    }

    return !_out_data.empty();
}


void IDLC::getInboundData( vector<unsigned char> &buf )
{
    buf.assign(_in_data.begin(), _in_data.end());
}


unsigned IDLC::getMaximumPayload( void ) const
{
    return Frame_MaximumDataLength;
}


}
}

