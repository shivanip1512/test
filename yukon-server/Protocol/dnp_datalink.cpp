/*-----------------------------------------------------------------------------*
*
* File:   dnp_datalink
*
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "porter.h"
#include "prot_dnp.h"
#include "dnp_datalink.h"

CtiDNPDatalink::CtiDNPDatalink()
{
    _io_state      = State_IO_Uninitialized;
    _control_state = State_Control_Ready;
    _dl_confirm    = false;
}

CtiDNPDatalink::CtiDNPDatalink(const CtiDNPDatalink &aRef)
{
    *this = aRef;
}

CtiDNPDatalink::~CtiDNPDatalink()   {}

CtiDNPDatalink &CtiDNPDatalink::operator=(const CtiDNPDatalink &aRef)
{
    if( this != &aRef )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *this;
}


void CtiDNPDatalink::setAddresses( unsigned short dst, unsigned short src)
{
    _dst = dst;
    _src = src;
}


void CtiDNPDatalink::setOptions(int options)
{
    if( options & CtiProtocolDNP::DatalinkConfirm )
    {
        _dl_confirm = true;
    }
}


void CtiDNPDatalink::resetLink( void )
{
    _reset_sent = false;
/*    _fcb_in     = false;
    _fcb_out    = false;*/
}


void CtiDNPDatalink::setToOutput(unsigned char *buf, unsigned int len)
{
    //  if it's too big or there's nothing to copy, set our buffer to zip
    if( len > Packet_MaxPayloadLen || buf == NULL )
    {
        _out_data_len   = 0;
        _io_state       = State_IO_Failed;
        _control_state  = State_Control_Ready;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        _io_state       = State_IO_Output;

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


void CtiDNPDatalink::setToInput( void )
{
    _io_state       = State_IO_Input;

    _control_state  = State_Control_Ready;

    _in_recv     = 0;
    _in_expected = 0;
    _in_actual   = 0;

    _in_data_len = 0;

    _comm_errors     = 0;
    _protocol_errors = 0;
}


int CtiDNPDatalink::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    if( isControlPending() )
    {
        generateControl(xfer);
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _io_state       = State_IO_Failed;

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


int CtiDNPDatalink::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;
    int toCopy, srcLen, packetSize;
    unsigned char *dst, *src;

    if( status != NORMAL )
    {
        switch( status )
        {
            case BADPORT:
            case PORTWRITE:
            case PORTREAD:
            default:
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        //if( ++_comm_errors >= CommRetryCount )
        //{
            _io_state       = State_IO_Failed;

            retVal    = status;
        //}
    }
    else
    {
        if( isControlPending() )
        {
            if( !_dl_confirm )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - datalink control message received, but DL confirm is not enabled for this devicetype **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            decodeControl(xfer, status);
        }
        else
        {
            //  ACH:  unified packet input/output/error checking...  um yeah.
            //          another layer, perhaps... ?  but a local, internal-ish one.

            switch( _io_state )
            {
                case State_IO_Input:
                {
                    //  ACH:  this is dumb...  i don't like this variable floating around...
                    //          we should only be dealing with whole packets

                    //  THIS IS PATENTLY FALSE - THIS CANNOT BE SET UNTIL WE HAVE VERIFIED THE INPUT
                    _in_recv += _in_actual;

                    if( isEntirePacket(_packet, _in_recv) )
                    {
                        //  check to see if the packet is okay
                        if( processControl(_packet) )
                        {
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
                    //  if we sent the whole packet okay
                    //  !!!  this is pointless, this will always evaluate true !!!
                    if( !status )
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
                    }
                    else
                    {
                        _comm_errors++;
                    }

                    //  otherwise, we'll regenerate and send again

                    break;
                }

                case State_IO_OutputRecvAck:
                {
                    //  THIS IS PATENTLY FALSE - THIS CANNOT BE SET UNTIL WE HAVE VERIFIED THE INPUT
                    _in_recv += _in_actual;

                    if( isEntirePacket(_packet, _in_recv) )
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

                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }
            }
        }
    }

    if( _protocol_errors > ProtocolRetryCount )
    {
        _io_state = State_IO_Failed;
        //  ACH:  set retVal here
    }

    return retVal;
}


void CtiDNPDatalink::constructDataPacket( dnp_datalink_packet &packet, unsigned char *buf, unsigned long len )
{
    int pos, block_len, num_blocks;
    unsigned short crc;
    unsigned char  *current_block, *crc_pos;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = Packet_HeaderLenCounted;  //  add on the header length
    packet.header.fmt.len += len;                      //  add the data length

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.p.direction = 1;  //  from the master
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
    packet.header.fmt.crc = computeCRC(packet.header.raw, 8);

    pos = 0;

    while( pos < len )
    {
        current_block = packet.data.blocks[pos/16];
        block_len     = len - pos;

        if( block_len > 16 )
        {
            block_len = 16;
        }

        //  copy in this block of data
        memcpy(current_block, &buf[pos], block_len);

        //  tack on the CRC
        crc = computeCRC(current_block, block_len);

        memcpy(current_block + block_len, &crc, sizeof(unsigned short));

        pos += block_len;
    }
}


void CtiDNPDatalink::constructPrimaryControlPacket( dnp_datalink_packet &packet, PrimaryControlFunction function, bool fcv, bool fcb )
{
    unsigned short crc;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = Packet_HeaderLenCounted;  //  control packets are empty

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.p.direction = 1;  //  from the master
    packet.header.fmt.control.p.primary   = 1;  //  this is a primary message

    packet.header.fmt.control.p.functionCode = function;
    packet.header.fmt.control.p.fcv          = fcv;
    packet.header.fmt.control.p.fcb          = fcb;

    //  tack on the CRC
    packet.header.fmt.crc = computeCRC(packet.header.raw, 8);
}


void CtiDNPDatalink::constructSecondaryControlPacket( dnp_datalink_packet &packet, SecondaryControlFunction function, bool dfc )
{
    unsigned short crc;

    packet.header.fmt.framing[0] = 0x05;
    packet.header.fmt.framing[1] = 0x64;

    packet.header.fmt.len  = Packet_HeaderLenCounted;  //  control packets are empty

    packet.header.fmt.destination = _dst;
    packet.header.fmt.source      = _src;

    packet.header.fmt.control.s.direction = 1;  //  from the master
    packet.header.fmt.control.s.primary   = 0;  //  secondary control
    packet.header.fmt.control.s.dfc       = !dfc; //  (negative logic - 1 means stop, 0 means go)
    packet.header.fmt.control.s.zpad      = 0;

    packet.header.fmt.control.s.functionCode = function;

    //  tack on the CRC
    packet.header.fmt.crc = computeCRC(packet.header.raw, 8);
}


void CtiDNPDatalink::sendPacket( dnp_datalink_packet &packet, CtiXfer &xfer )
{
    xfer.setOutBuffer((unsigned char *)&packet);
    xfer.setOutCount(calcPacketLength(packet.header.fmt.len));
    xfer.setCRCFlag(0);

    xfer.setInBuffer(NULL);
    xfer.setInCountActual(&_in_actual);
    xfer.setInCountExpected(0);
}


void CtiDNPDatalink::recvPacket( dnp_datalink_packet &packet, CtiXfer &xfer)
{
    if( _in_recv < Packet_HeaderLen )
    {
        //  get the header first so we know how much to expect
        _in_expected = Packet_HeaderLen - _in_recv;
    }
    else if( packet.header.fmt.framing[0] != 0x05 ||
             packet.header.fmt.framing[1] != 0x64 )
    {
        int offset;

        //  look for the framing bytes
        for( offset = 0; offset < _in_recv; offset++ )
        {
            //  if we can look for both framing bytes at once...
            if( (offset + 1) < _in_recv )
            {
                //  if we found the start of the header
                if( packet.header.raw[offset]   == 0x05 &&
                    packet.header.raw[offset+1] == 0x64 )
                {
                    //  move everything to the start of the packet
                    for( int i = 0; i < (_in_recv - offset); i++ )
                    {
                        packet.header.raw[i] = packet.header.raw[offset+i];
                    }

                    break;
                }
            }
            else  //  otherwise just look for 0x05
            {
                if( packet.header.raw[offset]   == 0x05 )
                {
                    //  just the one byte, that's easy enough to move
                    packet.header.raw[0] = 0x05;

                    break;
                }
            }
        }

        //  adjust our count to reflect valid bytes so far
        _in_recv -= offset;

        _in_expected = Packet_HeaderLen - _in_recv;
    }
    else
    {
        //  this can safely calculate to 0 if we mistakenly got in here
        _in_expected = calcPacketLength(packet.header.fmt.len) - _in_recv;
    }

    xfer.setInBuffer((unsigned char *)&packet + _in_recv);
    xfer.setInCountExpected(_in_expected);
    xfer.setInCountActual(&_in_actual);

    xfer.setOutBuffer(NULL);
    xfer.setOutCount(0);
    xfer.setCRCFlag(0);
}


bool CtiDNPDatalink::isControlPending( void )
{
    return (_control_state != State_Control_Ready);
}


bool CtiDNPDatalink::processControl( const dnp_datalink_packet &packet )
{
    bool retVal = false;

    if( areCRCsValid(packet) )
    {
        if( packet.header.fmt.control.p.direction == 0 )  //  incoming message
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
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

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
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

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

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            break;
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

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


void CtiDNPDatalink::generateControl( CtiXfer &xfer )
{
    switch( _control_state )
    {
        case State_Control_Request_DLReset_Out:
        {
            _fcb_out = true;

            constructPrimaryControlPacket(_control_packet, Control_PrimaryResetLink, false, false);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Request_DLReset_In:
        {
            recvPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Request_LinkStatus_Out:
        {
            constructPrimaryControlPacket(_control_packet, Control_PrimaryLinkStatus, true, _fcb_out);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_ResetLink:
        case State_Control_Reply_Ack:
        {
            constructSecondaryControlPacket(_control_packet, Control_SecondaryACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_Nack:
        {
            constructSecondaryControlPacket(_control_packet, Control_SecondaryNACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_NonFCBAck:
        {
            constructSecondaryControlPacket(_control_packet, Control_SecondaryACK, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Reply_LinkStatus:
        {
            constructSecondaryControlPacket(_control_packet, Control_SecondaryLinkStatus, true);

            sendPacket(_control_packet, xfer);

            break;
        }

        case State_Control_Ready:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "unhandled state = " << _control_state << endl;
            }

            break;
        }
    }
}


void CtiDNPDatalink::decodeControl( CtiXfer &xfer, int status )
{
    if( !status )
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

                if( isEntirePacket(_control_packet, _in_recv) )
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
                if( isEntirePacket(_control_packet, _in_recv) )
                {
                    if( areCRCsValid(_control_packet) &&
                        _control_packet.header.fmt.control.s.direction    == 0 &&
                        _control_packet.header.fmt.control.s.primary      == 0 &&
                        _control_packet.header.fmt.control.s.functionCode == Control_SecondaryLinkStatus )
                    {
                        if( _control_packet.header.fmt.control.s.dfc == 0 )
                        {
                            _in_recv       = 0;
                            _control_state = State_Control_Ready;
                        }
                        else
                        {
                            _protocol_errors++;

                            _control_state = State_Control_Request_LinkStatus_Out;
                        }
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "unhandled state = " << _control_state << endl;
                }

                break;
            }
        }
    }
}


bool CtiDNPDatalink::isValidDataPacket( const dnp_datalink_packet &packet )
{
    bool retVal = false;

    if( areCRCsValid( packet ) )
    {
        if( packet.header.fmt.control.p.primary   == 1 &&
            packet.header.fmt.control.p.direction == 0 )
        {
            if( !packet.header.fmt.control.p.fcv ||
                (packet.header.fmt.control.p.fcv && packet.header.fmt.control.p.fcb == _fcb_in) )
            {
                if( packet.header.fmt.control.p.functionCode == Control_PrimaryUserDataConfirmed ||
                    packet.header.fmt.control.p.functionCode == Control_PrimaryUserDataUnconfirmed )
                {
                    retVal = true;
                }
            }
        }
    }

    return retVal;
}


bool CtiDNPDatalink::isValidAckPacket( const dnp_datalink_packet &packet )
{
    bool retVal = false;

    if( areCRCsValid( packet ) )
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


bool CtiDNPDatalink::isTransactionComplete( void )
{
    bool retVal;

    if( (_io_state == State_IO_Complete) && !isControlPending() )
    {
        retVal = true;
    }
    else if( _io_state == State_IO_Uninitialized )
    {
        retVal = true;
    }
    else if( _io_state == State_IO_Failed )
    {
        retVal = true;
    }
    else
    {
        retVal = false;
    }

    return retVal;
}


bool CtiDNPDatalink::errorCondition( void )
{
    return _io_state == State_IO_Failed;
}


int CtiDNPDatalink::calcPacketLength( int headerLen )
{
    int packetLength, dataLength, numBlocks;

    if( headerLen > Packet_HeaderLenCounted)
    {
        //  get the true payload size by subtracting off the header bytes
        dataLength = headerLen - Packet_HeaderLenCounted;

        numBlocks  =  dataLength / 16;
        numBlocks += (dataLength % 16)?1:0;

        packetLength  = Packet_HeaderLen;
        packetLength += dataLength;
        packetLength += numBlocks * 2;  //  add on the CRC bytes
    }
    else
    {
        packetLength = Packet_HeaderLen;
    }

    return packetLength;
}


bool CtiDNPDatalink::isEntirePacket( const dnp_datalink_packet &packet, unsigned long in_recv )
{
    bool retVal = false;

    if( in_recv >= Packet_HeaderLen )
    {
        if( calcPacketLength(packet.header.fmt.len) == in_recv )
        {
            retVal = true;
        }
    }

    return retVal;
}


int CtiDNPDatalink::getInPayloadLength( void )
{
    return _in_data_len;
}


void CtiDNPDatalink::getInPayload( unsigned char *buf )
{
    memcpy(buf, _in_data, _in_data_len);
}


bool CtiDNPDatalink::areCRCsValid( const dnp_datalink_packet &packet )
{
    bool valid;

    int pos, len, block_len;
    unsigned short crc, block_crc;

    const unsigned char *current_block;


    //  default to true, set to false if any don't match
    valid = true;

    //  compute the header's CRC
    crc = computeCRC((unsigned char *)&packet.header, 8);

    if( crc != packet.header.fmt.crc )
    {
        valid = false;
    }
    else
    {
        pos = 0;
        len = packet.header.fmt.len - Packet_HeaderLenCounted;

        while( pos < len && valid )
        {
            current_block = packet.data.blocks[pos/16];
            block_len     = len - pos;

            if( block_len > 16 )
            {
                block_len = 16;
            }

            //  copy in this block of data
            crc = computeCRC(current_block, block_len);

            pos += block_len;

            //  snag the CRC from the end of the block
            memcpy((unsigned char *)&block_crc, current_block + block_len, sizeof(unsigned short));

            //  compare the CRCs
            if( crc != block_crc )
            {
                valid = false;
            }
        }
    }

    return valid;
}


void CtiDNPDatalink::putPacketPayload( const dnp_datalink_packet &packet, unsigned char *buf, int *len )
{
    unsigned const char *current_block;
    int payload_len, block_len, pos;

    //  subtract the header length
    payload_len = packet.header.fmt.len - Packet_HeaderLenCounted;

    if( buf != NULL )
    {
        pos = 0;

        while( pos < payload_len )
        {
            current_block = packet.data.blocks[pos/16];

            block_len = payload_len - pos;

            if( block_len > 16 )
            {
                block_len = 16;
            }

            memcpy(buf + pos, current_block, block_len);

            pos += block_len;
        }

        *len = payload_len;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Buffer passed to putPayload() is NULL... ?" << endl;
        }

        *len = 0;
    }
}


unsigned short CtiDNPDatalink::computeCRC( const unsigned char *buf, int len )
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

    unsigned short crc = 0;
    unsigned char index;

    for( int i = 0; i < len; i++ )
    {
        index = (crc ^ buf[i]) & 0x00ff;
        crc >>= 8;
        crc  ^= crctable[index];
    }

    return ~crc;
}



