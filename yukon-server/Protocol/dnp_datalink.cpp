#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/02/12 01:16:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "porter.h"
#include "prot_dnp.h"
#include "dnp_datalink.h"

CtiDNPDatalink::CtiDNPDatalink()
{
    _io_state   = State_Uninitialized;
    _dl_confirm = false;
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
    _fcb_in     = false;
    _fcb_out    = false;
}


void CtiDNPDatalink::setToOutput(unsigned char *buf, unsigned int len)
{
    _comm_errors     = 0;
    _protocol_errors = 0;

    //  if it's too big or there's nothing to copy, set our buffer to zip
    if( len > Packet_MaxPayloadLen || buf == NULL )
    {
        _out_data_len = 0;
        _io_state      = State_Failed;
    }
    else
    {
        _out_data_len = len;
        _io_state      = State_Output;

        memcpy(_out_data, buf, _out_data_len);
    }

    _out_sent = 0;
}


void CtiDNPDatalink::setToInput( void )
{
    _io_state = State_Input;

    _in_recv     = 0;
    _in_expected = 0;
    _in_actual   = 0;

    _comm_errors     = 0;
    _protocol_errors = 0;
}


int CtiDNPDatalink::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    if( isDatalinkControlActionPending() )
    {
        doDatalinkControlAction(xfer);
    }
    else
    {
        switch( _io_state )
        {
            case State_Output:
            {
                //  ACH:  retries if we're NACK'd or we time out
                constructPacket(&_packet, _out_data, _out_data_len);

                sendPacket(&_packet, xfer);

                break;
            }

            case State_Input:
            {
                recvPacket(&_packet, xfer);

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _io_state = State_Failed;
            }
        }
    }

    return retVal;
}


void CtiDNPDatalink::constructPacket( _dnp_datalink_packet *packet, unsigned char *buf, unsigned long len )
{
    int pos, block_len, num_blocks;
    unsigned short crc;
    unsigned char  *current_block, *crc_pos;

    packet->header.fmt.framing[0] = 0x05;
    packet->header.fmt.framing[1] = 0x64;

    packet->header.fmt.len  = Packet_HeaderLen;  //  add on the header length
    packet->header.fmt.len += len;               //  add the data length

    //  add on the CRC lengths if there's data there
    if( len > 0 )
    {
        //  0 = 0;  1-16 = 1;  17-32 = 2;  etc
        num_blocks = ((len - 1) / 16) + 1;

        //  one CRC per block
        packet->header.fmt.len += num_blocks * 2;
    }

    packet->header.fmt.destination = _dst;
    packet->header.fmt.source      = _src;

    packet->header.fmt.control.p.direction = 1;  //  from the master
    packet->header.fmt.control.p.primary   = 1;  //  we're primary

    if( _dl_confirm )
    {
        packet->header.fmt.control.p.functionCode = Control_PrimaryUserDataConfirmed;
        packet->header.fmt.control.p.fcv = 1;
        packet->header.fmt.control.p.fcb = _fcb_out;
    }
    else
    {
        packet->header.fmt.control.p.functionCode = Control_PrimaryUserDataUnconfirmed;
        packet->header.fmt.control.p.fcv = 0;
        packet->header.fmt.control.p.fcb = 0;
    }

    //  tack on the CRC
    packet->header.raw.crc = computeCRC(packet->header.raw.buf, 8);

    pos = 0;

    while( pos < len )
    {
        current_block = packet->data.blocks[pos/16];
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


void CtiDNPDatalink::sendPacket(_dnp_datalink_packet *packet, CtiXfer &xfer)
{
    xfer.setInBuffer(NULL);
    xfer.setInCountActual(&_in_actual);
    xfer.setInCountExpected(0);

    xfer.setOutBuffer((unsigned char *)packet);
    xfer.setOutCount(packet->header.fmt.len + Packet_HeaderLenUncounted);
    xfer.setCRCFlag(0);
}


void CtiDNPDatalink::recvPacket(_dnp_datalink_packet *packet, CtiXfer &xfer)
{
    if( _in_recv < Packet_HeaderLen )
    {
        if( _in_recv == 0 )
        {
            _in_expected = Packet_HeaderLen;
        }
        else
        {
            //  we should've gotten a full packet header - what went wrong?
            //  this should be taken care of by setting inExpected to DNPDatalinkHeaderLen - anything less would be a timeout.
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _in_expected = Packet_HeaderLen - _in_recv;
        }
    }
    else
    {
        //  ACH:  check to see if the packet is valid first

        //  if we're sending the final ACK, this should calculate to 0
        _in_expected = calcPacketLength( packet->header.fmt.len ) - _in_recv;
    }

    xfer.setInBuffer((unsigned char *)packet + _in_recv);
    xfer.setInCountExpected(_in_expected);  //  get the header first so we know how much to expect
    xfer.setInCountActual(&_in_actual);
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( ++_comm_errors >= CommRetryCount )
        {
            _io_state = State_Failed;
            retVal   = status;
        }
    }
    else
    {
        switch( _io_state )
        {
            case State_Output:
            {
/*                if( _dl_confirm )
                {
                    if( (_in_actual == Packet_HeaderLen) &&
                        (_confirmPacket.control.s.functionCode == Control_SecondaryACK) )
                    {
                        _outSent += xfer.getOutCount();
                    }
                    else
                    {
                        ++_protocolErrorCount;

                        //  we should retry, since we didn't change the _io_state
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
                else*/
                {
                    _out_sent += xfer.getOutCount();
                }

                break;
            }

            case State_Input:
            {
                _send_confirm = false;

                _in_recv += _in_actual;

                if( _in_recv >= Packet_FramingLen && !areFramingBytesValid(_packet) )
                {
                    status = Error_BadFraming;
                }
                else if( _in_recv >= Packet_HeaderLen )
                {
                    if( _in_recv == calcPacketLength( _packet.header.fmt.len ) )
                    {
                        if( !areCRCsValid(_packet) )
                        {
                            status = Error_BadCRC;
                        }

                        if( status == NoError )
                        {
                            //  ACH:  add datalink control check
                            /*
                            if( _inPacket.header.control.p.functionCode == Control_PrimaryUserDataConfirmed )
                            {
                                _sendConfirm = true;

                                _confirmPacket.framing[0] = 0x05;
                                _confirmPacket.framing[1] = 0x64;

                                _confirmPacket.control.s.direction    = 1;
                                _confirmPacket.control.s.primary      = 0;
                                _confirmPacket.control.s.dfc          = 0;
                                _confirmPacket.control.s.functionCode = Control_SecondaryACK;
                                _confirmPacket.control.s.zpad         = 0;

                                _confirmPacket.source      = _src;
                                _confirmPacket.destination = _dst;
                                _confirmPacket.len = 5;

                                _confirmPacket.crc = computeCRC(_confirmPacket.header.raw, 8);

                                if( _inPacket.header.control.p.fcv && (_inPacket.header.control.p.fcb != _fcbIn) )
                                {
                                    //  reset, we just got a duplicate
                                    _inRecv = 0;
                                    _fcbIn = !(_inPacket.header.control.p.fcb);
                                }
                            }
                            */
                            //  ACH: additional actions
                            //else if( _inPacket.header.control.p.functionCode == Primary_LinkStatus )
                        }
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
            }
        }

        if( _protocol_errors > ProtocolRetryCount )
        {
            _io_state = State_Failed;
        }
    }

    return retVal;
}


bool CtiDNPDatalink::isDatalinkControlActionPending( void )
{
    return false;
}


void CtiDNPDatalink::doDatalinkControlAction( CtiXfer &xfer )
{
/*    case State_Output://DLReset:
    {
        if( (_inActual == HeaderLen) &&
            (_confirmPacket.control.s.functionCode == Control_SecondaryACK) )
        {
            _io_state = State_Output;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            ++_protocolErrorCount;
        }

        break;
    }


    if( _sendConfirm )
    {
        //  if we're in this state, the confirmPacket's been filled out
        xfer.setOutBuffer((unsigned char *)&_confirmPacket);
        xfer.setOutCount(HeaderLen);
        xfer.setCRCFlag(0);

        _sendConfirm = false;
    }
    else
    {
        xfer.setOutBuffer((unsigned char *)&_outPacket);
        xfer.setOutCount(0);
        xfer.setCRCFlag(0);
    }



    //  ACH:  retries if we're NACK'd or we time out

    //  send the datalink reset packet
    _confirmPacket.framing[0] = 0x05;
    _confirmPacket.framing[1] = 0x64;

    _confirmPacket.control.p.direction    = 1;
    _confirmPacket.control.p.primary      = 1;
    _confirmPacket.control.p.fcb          = _fcbOut;
    _confirmPacket.control.p.fcv          = 0;
    _confirmPacket.control.p.functionCode = Primary_ResetLink;

    _confirmPacket.source      = _src;
    _confirmPacket.destination = _dst;
    _confirmPacket.len = 5;

    _confirmPacket.crc = computeCRC((unsigned char *)&_confirmPacket, 8);

    xfer.setOutBuffer((unsigned char *)&_confirmPacket);
    xfer.setOutCount(DNPDatalinkHeaderLen);
    xfer.setCRCFlag(0);

    //  receive the DL reset ACK packet into _confirmPacket
    xfer.setInBuffer((unsigned char *)&_confirmPacket);
    xfer.setInCountExpected(DNPDatalinkHeaderLen);
    xfer.setInCountActual(&_inActual);

    break;
*/
}


bool CtiDNPDatalink::isTransactionComplete( void )
{
    bool retVal = false;

    switch( _io_state )
    {
        case State_Output:
        {
            //  ACH: modify to wait for inbound ACK when secondary enabled
            if( _out_data_len == _out_sent )
            {
                retVal = true;
                _fcb_out = !_fcb_out;
            }

            break;
        }

        case State_Input:
        {
            if( _in_recv >= Packet_HeaderLen )
            {
                if( _in_recv == calcPacketLength( _packet.header.fmt.len ) )
                {
                    retVal = true;
                }
            }

            break;
        }

        default:
        {
            //  if we're uninitialized, we had nothing to do - so we're finished
            retVal = true;

            break;
        }
    }

    return retVal;
}


bool CtiDNPDatalink::errorCondition( void )
{
    return _io_state == State_Failed;
}


int CtiDNPDatalink::calcPacketLength( int headerLen )
{
    int packetLength, dataLength, numBlocks;

    //  get the true payload size by subtracting off the header bytes
    dataLength = headerLen - Packet_HeaderLenCounted;

    numBlocks  =  dataLength / 16;
    numBlocks += (dataLength % 16)?1:0;

    packetLength  = Packet_HeaderLen;
    packetLength += dataLength;
    packetLength += numBlocks * 2;  //  add on the CRC bytes

    return packetLength;
}


int CtiDNPDatalink::getPayloadLength( void )
{
    return _packet.header.fmt.len - Packet_HeaderLenCounted;
}


int CtiDNPDatalink::getPayload(unsigned char *buf)
{
    int retVal = NoError;

    unsigned char *current_block;
    int payload_len, block_len, pos;

    //  subtract the header length
    payload_len = _packet.header.fmt.len - Packet_HeaderLenCounted;

    if( buf != NULL )
    {
        pos = 0;

        while( pos < payload_len )
        {
            current_block = _packet.data.blocks[pos/16];

            block_len = payload_len - pos;

            if( block_len > 16 )
            {
                block_len = 16;
            }

            memcpy(buf + pos, current_block, block_len);

            pos += block_len;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Buffer passed to getInPayload() is NULL... ?" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


bool CtiDNPDatalink::areCRCsValid( const _dnp_datalink_packet &packet )
{
    bool valid;

    int pos, len, block_len;
    unsigned short crc, block_crc;

    const unsigned char *current_block;


    //  default to true, set to false if any don't match
    valid = true;

    //  compute the header's CRC
    crc = computeCRC(packet.header.raw.buf, 8);

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


bool CtiDNPDatalink::areFramingBytesValid( const _dnp_datalink_packet &packet )
{
    bool retVal = false;

    if( packet.header.fmt.framing[0] == 0x05 &&
        packet.header.fmt.framing[1] == 0x64 )
    {
        retVal = true;
    }

    return retVal;
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



