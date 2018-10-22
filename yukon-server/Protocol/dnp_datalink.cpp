#include "precompiled.h"

#include "dllbase.h"
#include "logger.h"
#include "porter.h"
#include "prot_dnp.h"
#include "dnp_datalink.h"

using std::endl;
using std::vector;

namespace Cti::Protocols::DNP {

DatalinkLayer::DatalinkLayer() :
    _io_state(State_IO_Uninitialized),
    _errorCondition(ClientErrors::None),
    _control_state(State_Control_Ready),
    _src(0),
    _dst(0),
    _dl_confirm(false),
    _slave_response(false),
    _send_confirm(false),
    _reset_sent(false),
    _fcb_out(false),
    _fcb_in(false),
    _comm_errors(0),
    _protocol_errors(0),
    _out_sent(0),
    _out_data_len(0),
    _in_recv(0),
    _in_expected(0),
    _in_actual(0),
    _in_data_len(0)
{
    memset( _out_data, 0, Packet_MaxPayloadLen );
    memset( _in_data,  0, Packet_MaxPayloadLen );

    memset( &_packet,         0, sizeof(packet_t) );
    memset( &_control_packet, 0, sizeof(packet_t) );
}

DatalinkLayer::DatalinkLayer(Slave) :
    DatalinkLayer()
{
    _slave_response = true;
}

void DatalinkLayer::setAddresses( unsigned short dst, unsigned short src)
{
    _dst = dst;
    _src = src;
}


unsigned short DatalinkLayer::getSrcAddress() const  {  return _src;  }
unsigned short DatalinkLayer::getDstAddress() const  {  return _dst;  }


void DatalinkLayer::setDatalinkConfirm()
{
    _dl_confirm = true;
}


void DatalinkLayer::setToLoopback()
{
    _io_state        = State_IO_Complete;
    _errorCondition  = ClientErrors::None;
    _control_state   = State_Control_Request_LinkStatus_Out;
    _comm_errors     = 0;
    _protocol_errors = 0;
}


void DatalinkLayer::setToOutput( unsigned char *buf, unsigned int len )
{
    //  if it's too big or there's nothing to copy, set our buffer to zip
    if( len > DatalinkPacket::PayloadLengthMax || buf == NULL )
    {
        _out_data_len   = 0;
        _io_state       = State_IO_Failed;
        _errorCondition = ClientErrors::Memory;
        _control_state  = State_Control_Ready;

        CTILOG_WARN(dout, "len > DatalinkPacket::PayloadLengthMax or buf is NULL");
    }
    else
    {
        _io_state       = State_IO_Output;
        _errorCondition = ClientErrors::None;

        if( _dl_confirm & !_reset_sent )
        {
            _control_state = State_Control_Request_DLReset_Out;
        }
        else
        {
            _control_state = State_Control_Ready;
        }

        _out_data_len  = len;
        _out_sent      = 0;     //  unused, eh?  we mostly just switch off of comm errors

        _comm_errors     = 0;
        _protocol_errors = 0;

        memcpy(_out_data, buf, _out_data_len);
    }
}


void DatalinkLayer::setToInput( void )
{
    _io_state       = State_IO_Input;
    _errorCondition = ClientErrors::None;

    _control_state  = State_Control_Ready;

    _in_recv     = 0;
    _in_expected = 0;
    _in_actual   = 0;

    _in_data_len = 0;

    _comm_errors     = 0;
    _protocol_errors = 0;
}


YukonError_t DatalinkLayer::generate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    if( isControlPending() )
    {
        //  do we need to add error handling for control-level errors?
        retVal = generateControl(xfer);
    }
    else
    {
        switch( _io_state )
        {
            case State_IO_Input:
            {
                recvPacket(_packet, xfer);

                break;
            }

            case State_IO_Output:
            {
                constructDataPacket(_packet, _out_data, _out_data_len);

                sendPacket(_packet, xfer);

                break;
            }

            //  ACH:  does this need to just be another control state transition?
            case State_IO_OutputRecvAck:
            {
                recvPacket(_packet, xfer);

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unhandled state "<< _io_state);
                //  fall through
            }
            case State_IO_Failed:
            {
                _errorCondition = retVal = ClientErrors::Abnormal;

                xfer.setOutBuffer(NULL);
                xfer.setOutCount(0);
                xfer.setInBuffer((unsigned char *)&_packet);
                xfer.setInCountActual(&_in_actual);
                xfer.setInCountExpected(0);
            }
        }
    }

    return retVal;
}


YukonError_t DatalinkLayer::decode( CtiXfer &xfer, YukonError_t status )
{
    if( status )
    {
        //  we need to be able to handle errors at the datalink control level, too - this is too simplistic

        switch( status )
        {
            case ClientErrors::BadPort:
            case ClientErrors::PortWrite:
            case ClientErrors::PortRead:
                break;

            default:
            {
                if( isDebugLudicrous() )
                {
                    CTILOG_DEBUG(dout, "unexpected error "<< status <<" on port");
                }
            }
        }

        //  add retries
        _io_state = State_IO_Failed;
        return _errorCondition = status;
    }

    YukonError_t retVal = ClientErrors::None;

    if( isControlPending() )
    {
        //  we're in State_IO_Complete if it's a loopback
        if( _io_state != State_IO_Complete )
        {
            if( !_dl_confirm )
            {
                CTILOG_WARN(dout, "datalink control message received, but DL confirm is not enabled for this devicetype");
            }
        }

        //  Can't return yet - decodeControl could increment _protocol_errors, which is checked at the bottom of this method
        retVal = decodeControl(xfer, status);
    }
    else
    {
        //  ACH:  unified packet input/output/error checking...  um yeah.
        //          another layer, perhaps... ?  but a local, internal-ish one.

        switch( _io_state )
        {
            case State_IO_Input:
            {
                _in_recv += decodePacket(xfer, _packet, _in_recv);

                //  if we're NOT datalink confirm, we fail immediately on a bad CRC;
                //    otherwise, we pass the whole packet to processControl and let them fail it
                if( !_dl_confirm && _in_recv >= DatalinkPacket::HeaderLength && !isHeaderCRCValid(_packet.header) )
                {
                    _io_state = State_IO_Failed;

                    return _errorCondition = ClientErrors::BadCrc;
                }
                //  a possible optimization could be that all control packets are known
                //    to be DatalinkPacket::HeaderLength long, so we don't need the isEntirePacket()
                //    check here in addition to processControl()
                if( DatalinkPacket::isEntirePacket(_packet, _in_recv) )
                {
                    //  see above note about CRC checking
                    if( !_dl_confirm && !arePacketCRCsValid(_packet) )
                    {
                        _io_state = State_IO_Failed;

                        return _errorCondition = ClientErrors::BadCrc;
                    }
                    //  check to see if the packet is okay
                    if( processControl(_packet) )
                    {
                        if( _packet.header.fmt.destination != _src ||
                            _packet.header.fmt.source      != _dst )
                        {
                            CTILOG_WARN( dout, "DNP Protocol error, incorrect address received.  Expecting (" 
                                << _src << ", " << _dst << "), received (" << _packet.header.fmt.destination << ", " 
                                << _packet.header.fmt.source << ")" );

                            _io_state = State_IO_Failed;

                            return _errorCondition = ClientErrors::WrongAddress;
                        }

                        putPacketPayload(_packet, _in_data, &_in_data_len);

                        _io_state = State_IO_Complete;
                    }
                    else
                    {
                        //  we'll count this as an error, even though we might've just gotten a data link reset -
                        //    we don't want to get stuck in a loop
                        _protocol_errors++;

                        //  then start us listening all over again
                        _in_recv = 0;
                    }
                }

                break;
            }

            case State_IO_Output:
            {
                if( _dl_confirm )
                {
                    _io_state = State_IO_OutputRecvAck;
                    _in_recv  = 0;
                }
                else
                {
                    _io_state = State_IO_Complete;
                }

                break;
            }

            case State_IO_OutputRecvAck:
            {
                _in_recv += decodePacket(xfer, _packet, _in_recv);

                if( DatalinkPacket::isEntirePacket(_packet, _in_recv) )
                {
                    //  check to see if there's a control message that needs to be worked on
                    if( processControl(_packet) )
                    {
                        _io_state = State_IO_Complete;
                    }
                    else
                    {
                        //  we'll count this as an error, even though we might've just gotten a data link reset -
                        //    we don't want to get stuck in a loop
                        _protocol_errors++;

                        //  then try the send again
                        _io_state = State_IO_Output;
                    }
                }

                break;
            }

            case State_IO_Complete:
            {
                CTILOG_DEBUG(dout, "DNP datalink state: Complete (" << _io_state << ")");

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unhandled state "<< _io_state);

                break;
            }
        }
    }

    if( _protocol_errors > ProtocolRetryCount )
    {
        _io_state = State_IO_Failed;
        _errorCondition = retVal = ClientErrors::Abnormal;
    }

    return retVal;
}


void DatalinkLayer::constructDataPacket( DatalinkLayer::packet_t &packet, unsigned char *buf, unsigned long len )
{
    int pos, block_len, num_blocks;
    unsigned short block_crc;
    unsigned char  *current_block, *crc_pos;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = DatalinkPacket::HeaderCountedLength;  //  add on the header length
    packet.header.fmt.len += len;                                  //  add the data length

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.p.direction = ! _slave_response;

    packet.header.fmt.control.p.primary   = 1;  //  we're primary

    if( _dl_confirm )
    {
        packet.header.fmt.control.p.functionCode = Control_PrimaryUserDataConfirmed;
        packet.header.fmt.control.p.fcv = 1;
        packet.header.fmt.control.p.fcb = _fcb_out;
    }
    else
    {
        packet.header.fmt.control.p.functionCode = Control_PrimaryUserDataUnconfirmed;
        packet.header.fmt.control.p.fcv = 0;
        packet.header.fmt.control.p.fcb = 0;
    }

    //  tack on the CRC
    packet.header.fmt.crc = crc(packet.header.raw, 8);

    pos = 0;

    while( pos < len )
    {
        current_block = packet.data.blocks[pos/DatalinkPacket::BlockLength];
        block_len     = len - pos;

        if( block_len > DatalinkPacket::BlockLength )
        {
            block_len = DatalinkPacket::BlockLength;
        }

        //  copy in this block of data
        memcpy(current_block, &buf[pos], block_len);

        //  compute the CRC
        block_crc = crc(current_block, block_len);

        //  jam it on the end, little-endian
        current_block[block_len + 0] = block_crc      & 0xff;
        current_block[block_len + 1] = block_crc >> 8 & 0xff;

        pos += block_len;
    }
}


auto DatalinkLayer::constructPrimaryControlPacket( PrimaryControlFunction function, bool fcv, bool fcb ) -> packet_t
{
    packet_t packet;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = DatalinkPacket::HeaderCountedLength;  //  control packets are empty

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.p.direction = ! _slave_response;
    packet.header.fmt.control.p.primary   = 1;  //  this is a primary message

    packet.header.fmt.control.p.functionCode = function;
    packet.header.fmt.control.p.fcv          = fcv;
    packet.header.fmt.control.p.fcb          = fcb;

    //  tack on the CRC
    packet.header.fmt.crc = crc(packet.header.raw, 8);

    return packet;
}


auto DatalinkLayer::constructSecondaryControlPacket( SecondaryControlFunction function, bool dfc ) -> packet_t
{
    packet_t packet;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = DatalinkPacket::HeaderCountedLength;  //  control packets are empty

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.s.direction = ! _slave_response;
    packet.header.fmt.control.s.primary   = 0;  //  secondary control
    packet.header.fmt.control.s.dfc       = !dfc; //  (negative logic - 1 means stop, 0 means go)
    packet.header.fmt.control.s.zpad      = 0;

    packet.header.fmt.control.s.functionCode = function;

    //  tack on the CRC
    packet.header.fmt.crc = crc(packet.header.raw, 8);

    return packet;
}


void DatalinkLayer::sendPacket( DatalinkLayer::packet_t &packet, CtiXfer &xfer )
{
    xfer.setOutBuffer((unsigned char *)&packet);
    xfer.setOutCount(DatalinkPacket::calcPacketLength(packet.header.fmt.len));
    xfer.setCRCFlag(0);

    xfer.setInBuffer(NULL);
    xfer.setInCountActual(&_in_actual);
    xfer.setInCountExpected(0);
}


void DatalinkLayer::recvPacket( DatalinkLayer::packet_t &packet, CtiXfer &xfer )
{
    if( _in_recv < DatalinkPacket::HeaderLength )
    {
        //  get the header first so we know how much to expect
        _in_expected = DatalinkPacket::HeaderLength - _in_recv;
    }
    else
    {
        //  this can safely calculate to 0 if we mistakenly got in here
        _in_expected = DatalinkPacket::calcPacketLength(packet.header.fmt.len) - _in_recv;
    }

    xfer.setInBuffer((unsigned char *)&packet + _in_recv);
    xfer.setInCountExpected(_in_expected);
    xfer.setInCountActual(&_in_actual);
    xfer.setInTimeout(1);  //  Give a minimum of at least 1 second for the receive, even if the bitrate is high and the data size is small.

    xfer.setOutBuffer(NULL);
    xfer.setOutCount(0);
    xfer.setCRCFlag(0);
}


bool DatalinkLayer::isControlPending( void ) const
{
    return (_control_state != State_Control_Ready);
}


bool DatalinkLayer::processControl( const DatalinkLayer::packet_t &packet )
{
    bool retVal = false;

    if( arePacketCRCsValid(packet) )
    {
        //  make sure it matches the direction we're configured for
        if( packet.header.fmt.control.p.direction == _slave_response )
        {
            if( packet.header.fmt.control.p.primary == 1 )  //  primary control message
            {
                if( packet.header.fmt.control.p.fcv )
                {
                    if( packet.header.fmt.control.p.fcb == _fcb_in )
                    {
                        retVal = true;

                        _control_state = State_Control_Reply_Ack;
                    }
                    else
                    {
                        switch( packet.header.fmt.control.p.functionCode )
                        {
                            case Control_PrimaryUserDataConfirmed:
                            {
                                _control_state = State_Control_Reply_Nack;

                                break;
                            }

                            case Control_PrimaryTestLink:
                            {
                                _control_state = State_Control_Reply_NonFCBAck;

                                break;
                            }

                            default:
                            {
                                CTILOG_INFO(dout, "packet.header.fmt.control.p.functionCode = "<< packet.header.fmt.control.p.functionCode);

                                _control_state = State_Control_Ready;

                                break;
                            }
                        }
                    }
                }
                else
                {
                    switch( packet.header.fmt.control.p.functionCode )
                    {
                        case Control_PrimaryResetLink:
                        {
                            _control_state = State_Control_Reply_ResetLink;

                            break;
                        }

                        case Control_PrimaryLinkStatus:
                        {
                            _control_state = State_Control_Reply_LinkStatus;

                            break;
                        }

                        case Control_PrimaryResetProcess:
                        {
                            _control_state = State_Control_Reply_Ack;

                            break;
                        }

                        case Control_PrimaryUserDataUnconfirmed:
                        {
                            _control_state = State_Control_Ready;

                            retVal = true;

                            break;
                        }

                        default:
                        {
                            CTILOG_INFO(dout, "packet.header.fmt.control.p.functionCode = "<< packet.header.fmt.control.p.functionCode);

                            break;
                        }
                    }
                }
            }
            else  //  packet.header.fmt.control.s.primary == 0  //  secondary message
            {
                if( packet.header.fmt.control.s.dfc == 0 )  //  flow control OK
                {
                    switch( packet.header.fmt.control.s.functionCode )
                    {
                        case Control_SecondaryACK:
                        {
                            _fcb_out = !_fcb_out;
                        }
                        case Control_SecondaryLinkStatus:
                        {
                            retVal = true;

                            _control_state = State_Control_Ready;

                            break;
                        }

                        case Control_SecondaryNACK:
                        default:
                        {
                            //  try again... ?
                            CTILOG_INFO(dout, "packet.header.fmt.control.s.functionCode = "<< packet.header.fmt.control.p.functionCode);
                        }
                    }
                }
                else
                {
                    CTILOG_INFO(dout, "packet.header.fmt.control.s.dfc = "<< packet.header.fmt.control.s.dfc)

                    _control_state = State_Control_Request_LinkStatus_Out;
                }
            }
        }
    }
    else
    {
        _control_state = State_Control_Reply_Nack;
    }

    return retVal;
}


YukonError_t DatalinkLayer::generateControl( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    switch( _control_state )
    {
        case State_Control_Request_DLReset_Out:
        {
            _fcb_out = true;

            _control_packet = constructPrimaryControlPacket(Control_PrimaryResetLink, false, false);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Request_DLReset_In:
        case State_Control_Request_LinkStatus_In:
        {
            recvPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Request_LinkStatus_Out:
        {
            _control_packet = constructPrimaryControlPacket(Control_PrimaryLinkStatus, true, _fcb_out);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_ResetLink:
        case State_Control_Reply_Ack:
        {
            _control_packet = constructSecondaryControlPacket(Control_SecondaryACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_Nack:
        {
            _control_packet = constructSecondaryControlPacket(Control_SecondaryNACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_NonFCBAck:
        {
            _control_packet = constructSecondaryControlPacket(Control_SecondaryACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_LinkStatus:
        {
            _control_packet = constructSecondaryControlPacket(Control_SecondaryLinkStatus, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Ready:
        default:
        {
            CTILOG_ERROR(dout, "unhandled state "<< _control_state);

            retVal = ClientErrors::Abnormal;
        }
    }

    return retVal;
}


YukonError_t DatalinkLayer::decodeControl( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retVal = ClientErrors::None;

    if( status )
    {
        retVal = status;
    }
    else
    {
        switch( _control_state )
        {
            case State_Control_Request_DLReset_Out:
            {
                _in_recv     = 0;
                _control_state = State_Control_Request_DLReset_In;

                break;
            }

            case State_Control_Request_DLReset_In:
            {
                //  this sucks - i need an intermediary.
                _in_recv += _in_actual;

                if( DatalinkPacket::isEntirePacket(_control_packet, _in_recv) )
                {
                    if( isValidAckPacket(_control_packet) )
                    {
                        if( _control_packet.header.fmt.control.s.dfc == 0 )
                        {
                            _in_recv       = 0;
                            _control_state = State_Control_Ready;
                        }
                        else
                        {
                            _control_state = State_Control_Request_LinkStatus_Out;
                        }
                    }
                    else
                    {
                        _protocol_errors++;

                        _control_state = State_Control_Request_DLReset_Out;
                    }
                }

                break;
            }

            case State_Control_Request_LinkStatus_Out:
            {
                _in_recv     = 0;
                _control_state = State_Control_Request_LinkStatus_In;

                break;
            }

            case State_Control_Request_LinkStatus_In:
            {
                _in_recv += _in_actual;

                if( DatalinkPacket::isEntirePacket(_control_packet, _in_recv) )
                {
                    if( arePacketCRCsValid(_control_packet) &&
                        _control_packet.header.fmt.control.s.direction    == 0 &&
                        _control_packet.header.fmt.control.s.primary      == 0 )
                    {
                        switch( _control_packet.header.fmt.control.s.functionCode )
                        {
                            case Control_SecondaryLinkStatus:
                            {
                                if( _control_packet.header.fmt.control.s.dfc == 0 )
                                {
                                    _in_recv = 0;
                                    _control_state = State_Control_Ready;
                                }
                                else
                                {
                                    _protocol_errors++;

                                    _control_state = State_Control_Request_LinkStatus_Out;
                                }
                                break;
                            }
                            default:
                            {
                                CTILOG_DEBUG(dout, "Received unexpected function code " << _control_packet.header.fmt.control.s.functionCode);
                                [[fallthrough]];
                            }
                            case Control_SecondaryNotSupported:
                            {
                                _control_state = State_Control_Ready;
                            }
                        }
                    }
                    else
                    {
                        _protocol_errors++;

                        _control_state = State_Control_Request_LinkStatus_Out;
                    }
                }
                break;
            }

            case State_Control_Reply_ResetLink:
            {
                _fcb_in = true;

                _in_recv       = 0;
                _control_state = State_Control_Ready;

                break;
            }

            case State_Control_Reply_Ack:
            {
                _fcb_in = !_fcb_in;
            }
            case State_Control_Reply_NonFCBAck:
            {
                _in_recv       = 0;
                _control_state = State_Control_Ready;

                break;
            }

            case State_Control_Reply_Nack:
            {
                _in_recv       = 0;
                _control_state = State_Control_Ready;

                break;
            }

            case State_Control_Reply_LinkStatus:
            {
                _in_recv       = 0;
                _control_state = State_Control_Ready;

                break;
            }

            case State_Control_Ready:
            default:
            {
                CTILOG_ERROR(dout, "unhandled state "<< _control_state);
            }
        }
    }

    return retVal;
}


int DatalinkLayer::decodePacket( CtiXfer &xfer, packet_t &p, unsigned long received )
{
    unsigned long in_actual = xfer.getInCountActual();

    if( !received )
    {
        unsigned long offset;

        //  look for the framing bytes
        for( offset = 0; offset < in_actual; offset++ )
        {
            if( p.header.raw[offset] == 0x05 )
            {
                //  if we can look for both framing bytes at once...
                if( (offset + 1) < in_actual )
                {
                    //  if we found the start of the header
                    if( p.header.raw[offset+1] == 0x64 )
                    {
                        //  move everything to the start of the packet
                        memmove(p.header.raw, p.header.raw + offset, in_actual - offset);

                        break;
                    }
                }
                else  //  otherwise just look for 0x05
                {
                    //  just the one byte, that's easy enough to move
                    p.header.raw[0] = 0x05;

                    break;
                }
            }
        }

        in_actual -= offset;
    }

    return in_actual;
}


bool DatalinkLayer::isValidAckPacket( const DatalinkLayer::packet_t &packet )
{
    bool retVal = false;

    if( arePacketCRCsValid( packet ) )
    {
        if( packet.header.fmt.control.s.primary   == 0 &&
            packet.header.fmt.control.s.direction == 0 )
        {
            if( packet.header.fmt.control.s.functionCode == Control_SecondaryACK )
            {
                retVal = true;
            }
        }
    }

    return retVal;
}


bool DatalinkLayer::isTransactionComplete( void )
{
    bool retVal = false;

    switch( _io_state )
    {
        case State_IO_Complete:
        {
            retVal = !isControlPending();
            break;
        }

        case State_IO_Uninitialized:
        case State_IO_Failed:
        {
            retVal = true;
            break;
        }
    }

    return retVal;
}

void DatalinkLayer::setIoStateComplete()
{
    _io_state = State_IO_Complete;
}

YukonError_t DatalinkLayer::errorCondition()
{
    return _errorCondition;
}


vector<unsigned char> DatalinkLayer::getInPayload( void ) const
{
    return vector<unsigned char>(_in_data,
                                 _in_data + _in_data_len);
}


bool DatalinkLayer::isHeaderCRCValid( const DatalinkPacket::dlp_header &header )
{
    unsigned length = DatalinkPacket::HeaderLength - DatalinkPacket::CRCLength;

    return crc(header.raw, length) == header.fmt.crc;
}


bool DatalinkLayer::isDataBlockCRCValid( const unsigned char *block, unsigned length )
{
    unsigned short block_crc = *(reinterpret_cast<const unsigned short *>(block + length));

    return crc(block, length) == block_crc;
}


bool DatalinkLayer::arePacketCRCsValid( const DatalinkLayer::packet_t &packet )
{
    bool crcs_valid = isHeaderCRCValid(packet.header);

    if( crcs_valid )
    {
        int packet_length = packet.header.fmt.len - DatalinkPacket::HeaderCountedLength;

        for( int pos = 0; crcs_valid && pos < packet_length; pos += DatalinkPacket::BlockLength )
        {
            //  note that each "block" is 18 bytes long, to allow for easy indexing of the
            //    data - 16 bytes of data + 2 bytes of CRC...
            //  we index by the data bytes we're looking at, not by the data+crc bytes
            const unsigned char *block = packet.data.blocks[pos/DatalinkPacket::BlockLength];

            int block_length = packet_length - pos;

            if( block_length > DatalinkPacket::BlockLength )
            {
                block_length = DatalinkPacket::BlockLength;
            }

            crcs_valid &= isDataBlockCRCValid(block, block_length);
        }
    }

    return crcs_valid;
}


void DatalinkLayer::putPacketPayload( const DatalinkLayer::packet_t &packet, unsigned char *buf, int *len )
{
    unsigned const char *current_block;
    int payload_len, block_len, pos;

    //  subtract the header length
    payload_len = packet.header.fmt.len - DatalinkPacket::HeaderCountedLength;

    if( buf != NULL )
    {
        pos = 0;

        while( pos < payload_len )
        {
            current_block = packet.data.blocks[pos/DatalinkPacket::BlockLength];

            block_len = payload_len - pos;

            if( block_len > DatalinkPacket::BlockLength )
            {
                block_len = DatalinkPacket::BlockLength;
            }

            memcpy(buf + pos, current_block, block_len);

            pos += block_len;
        }

        *len = payload_len;
    }
    else
    {
        CTILOG_ERROR(dout, "Buffer passed to putPayload() is NULL... ?");

        *len = 0;
    }
}


IM_EX_PROT bool DatalinkLayer::isPacketValid( const unsigned char *buf, const size_t len )
{
    if( len < DatalinkPacket::HeaderLength )
    {
        return false;
    }

    if( buf[0] != 0x05 ||
        buf[1] != 0x64 )
    {
        return false;
    }

    const packet_t *p = reinterpret_cast<const packet_t *>(buf);

    if( len < DatalinkPacket::calcPacketLength(p->header.fmt.len) )
    {
        return false;
    }

    if( !arePacketCRCsValid(*p) )
    {
        return false;
    }

    return true;
}


//  export it for the DNP UDP port and other general uses
IM_EX_PROT unsigned short DatalinkLayer::crc( const unsigned char *buf, const int len )
{
    //  this table and code taken from the DNP docs.
    //    original author Jim McFadyen

    static unsigned short crctable[256] =
    {
        0x0000,  0x365e,  0x6cbc,  0x5ae2,  0xd978,  0xef26,  0xb5c4,  0x839a,
        0xff89,  0xc9d7,  0x9335,  0xa56b,  0x26f1,  0x10af,  0x4a4d,  0x7c13,
        0xb26b,  0x8435,  0xded7,  0xe889,  0x6b13,  0x5d4d,  0x07af,  0x31f1,
        0x4de2,  0x7bbc,  0x215e,  0x1700,  0x949a,  0xa2c4,  0xf826,  0xce78,
        0x29af,  0x1ff1,  0x4513,  0x734d,  0xf0d7,  0xc689,  0x9c6b,  0xaa35,
        0xd626,  0xe078,  0xba9a,  0x8cc4,  0x0f5e,  0x3900,  0x63e2,  0x55bc,
        0x9bc4,  0xad9a,  0xf778,  0xc126,  0x42bc,  0x74e2,  0x2e00,  0x185e,
        0x644d,  0x5213,  0x08f1,  0x3eaf,  0xbd35,  0x8b6b,  0xd189,  0xe7d7,
        0x535e,  0x6500,  0x3fe2,  0x09bc,  0x8a26,  0xbc78,  0xe69a,  0xd0c4,
        0xacd7,  0x9a89,  0xc06b,  0xf635,  0x75af,  0x43f1,  0x1913,  0x2f4d,
        0xe135,  0xd76b,  0x8d89,  0xbbd7,  0x384d,  0x0e13,  0x54f1,  0x62af,
        0x1ebc,  0x28e2,  0x7200,  0x445e,  0xc7c4,  0xf19a,  0xab78,  0x9d26,
        0x7af1,  0x4caf,  0x164d,  0x2013,  0xa389,  0x95d7,  0xcf35,  0xf96b,
        0x8578,  0xb326,  0xe9c4,  0xdf9a,  0x5c00,  0x6a5e,  0x30bc,  0x06e2,
        0xc89a,  0xfec4,  0xa426,  0x9278,  0x11e2,  0x27bc,  0x7d5e,  0x4b00,
        0x3713,  0x014d,  0x5baf,  0x6df1,  0xee6b,  0xd835,  0x82d7,  0xb489,
        0xa6bc,  0x90e2,  0xca00,  0xfc5e,  0x7fc4,  0x499a,  0x1378,  0x2526,
        0x5935,  0x6f6b,  0x3589,  0x03d7,  0x804d,  0xb613,  0xecf1,  0xdaaf,
        0x14d7,  0x2289,  0x786b,  0x4e35,  0xcdaf,  0xfbf1,  0xa113,  0x974d,
        0xeb5e,  0xdd00,  0x87e2,  0xb1bc,  0x3226,  0x0478,  0x5e9a,  0x68c4,
        0x8f13,  0xb94d,  0xe3af,  0xd5f1,  0x566b,  0x6035,  0x3ad7,  0x0c89,
        0x709a,  0x46c4,  0x1c26,  0x2a78,  0xa9e2,  0x9fbc,  0xc55e,  0xf300,
        0x3d78,  0x0b26,  0x51c4,  0x679a,  0xe400,  0xd25e,  0x88bc,  0xbee2,
        0xc2f1,  0xf4af,  0xae4d,  0x9813,  0x1b89,  0x2dd7,  0x7735,  0x416b,
        0xf5e2,  0xc3bc,  0x995e,  0xaf00,  0x2c9a,  0x1ac4,  0x4026,  0x7678,
        0x0a6b,  0x3c35,  0x66d7,  0x5089,  0xd313,  0xe54d,  0xbfaf,  0x89f1,
        0x4789,  0x71d7,  0x2b35,  0x1d6b,  0x9ef1,  0xa8af,  0xf24d,  0xc413,
        0xb800,  0x8e5e,  0xd4bc,  0xe2e2,  0x6178,  0x5726,  0x0dc4,  0x3b9a,
        0xdc4d,  0xea13,  0xb0f1,  0x86af,  0x0535,  0x336b,  0x6989,  0x5fd7,
        0x23c4,  0x159a,  0x4f78,  0x7926,  0xfabc,  0xcce2,  0x9600,  0xa05e,
        0x6e26,  0x5878,  0x029a,  0x34c4,  0xb75e,  0x8100,  0xdbe2,  0xedbc,
        0x91af,  0xa7f1,  0xfd13,  0xcb4d,  0x48d7,  0x7e89,  0x246b,  0x1235
    };

    unsigned short new_crc = 0;
    unsigned char index;

    for( int i = 0; i < len; i++ )
    {
        index = (new_crc ^ buf[i]) & 0x00ff;
        new_crc >>= 8;
        new_crc  ^= crctable[index];
    }

    return ~new_crc;
}

}

