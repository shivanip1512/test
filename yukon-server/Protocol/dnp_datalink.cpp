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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:25 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "dnp_datalink.h"

CtiDNPDatalink::CtiDNPDatalink()    { }

CtiDNPDatalink::CtiDNPDatalink(const CtiDNPDatalink &aRef)
{
    *this = aRef;
}

CtiDNPDatalink::~CtiDNPDatalink()   { }

CtiDNPDatalink &CtiDNPDatalink::operator=(const CtiDNPDatalink &aRef)
{
    if( this != &aRef )
    {
        _packet = aRef._packet;
    }

    return *this;
}

unsigned short CtiDNPDatalink::computeCRC( unsigned char *buf, int len )
{
    //  this table and code taken from the DNP docs.
    //    original author Jim McFadyen

    static unsigned short crctable[256] = { 0x0000,  0x365e,  0x6cbc,  0x5ae2,  0xd978,  0xef26,  0xb5c4,  0x839a,
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
                                            0x91af,  0xa7f1,  0xfd13,  0xcb4d,  0x48d7,  0x7e89,  0x246b,  0x1235 };
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

unsigned short CtiDNPDatalink::getSourceAddress(void)
{
    return _packet.header.source;
}

void CtiDNPDatalink::setSourceAddress(unsigned short addr)
{
    _packet.header.source = addr;
}

unsigned short CtiDNPDatalink::getDestinationAddress(void)
{
    return _packet.header.destination;
}

void CtiDNPDatalink::setDestinationAddress(unsigned short addr)
{
    _packet.header.destination = addr;
}

unsigned short CtiDNPDatalink::getLength(void)
{
    return _packet.header.len;
}

unsigned char *CtiDNPDatalink::getMessage(void)
{
    //  remove CRCs, jam data together, yo

    int msgLen, copied, toCopy;
    unsigned char *msg;

    //  subtract the header length
    msgLen = _packet.header.len - 5;

    msg = new unsigned char[msgLen];

    if( msg != NULL )
    {
        copied = 0;

        while( copied < msgLen )
        {
            toCopy = msgLen - copied;

            if( toCopy > 16 )
                toCopy = 16;

            memcpy(&msg[copied], _packet.data.blocks[copied/16], toCopy);

            copied += toCopy;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Couldn't allocate memory to return DNP message" << endl;
        }
    }

    return msg;
}

int CtiDNPDatalink::setMessage(unsigned char *buf, unsigned char len)
{
    //  insert CRCs, etc etc

    int pos, blockLen;
    unsigned short crc, *crcPos;

    pos = 0;

    //  if it's too big or there's nothing to copy, set our buffer to zip
    if( len > 250 || buf == NULL )
        len = 0;

    while( pos < len )
    {
        blockLen = len - pos;

        if( blockLen > 16 )
            blockLen = 16;

        //  copy in this block of data
        memcpy(_packet.data.blocks[pos/16], &buf[pos], blockLen);

        //  figure out where the CRC should go
        crcPos = (unsigned short *)(_packet.data.blocks[pos/16] + blockLen);

        //  tack on the CRC
        *crcPos = computeCRC(_packet.data.blocks[pos/16], blockLen);

        pos += blockLen;
    }

    //  add on the header length
    _packet.header.len = len + 5;

    return pos;
}

int CtiDNPDatalink::areCRCsValid( void )
{
    int valid = TRUE;

    int blockLen, pos, len;
    unsigned short crc, blockCRC;

    //  compute the header's CRC
    crc = computeCRC((unsigned char *)(&_packet.header.control), 5);

    if(crc != _packet.header.crc)
    {
        valid = FALSE;
    }

    pos = 0;
    len = _packet.header.len - 5;

    while( pos < len && valid == TRUE )
    {
        blockLen = len - pos;

        if( blockLen > 16 )
            blockLen = 16;

        //  copy in this block of data
        crc = computeCRC(_packet.data.blocks[pos/16], blockLen);

        //  snag the CRC from the end of the block
        blockCRC = *((unsigned short *)(&_packet.data.blocks[pos/16][blockLen]));

        pos += blockLen;

        //  compare the CRCs
        if( crc != blockCRC )
        {
            valid = FALSE;
        }
    }

    return valid;
}

CtiDNPDatalink::DatalinkError CtiDNPDatalink::isValid( void )
{
    if( _packet.header.framing[0] != 0x64 &&
        05 )  //  will compare LSB-wise, 05 64
    {
        return BadFraming;
    }

    if( !areCRCsValid() )
    {
        return BadCRC;
    }
}

