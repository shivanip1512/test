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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:14:03 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "porter.h"
#include "dnp_datalink.h"

CtiDNPDatalink::CtiDNPDatalink()
{
    _ioState = Uninitialized;
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


void CtiDNPDatalink::reset( void )
{
    memset((void *)&_inPacket,  0, sizeof(_inPacket) );
    memset((void *)&_outPacket, 0, sizeof(_outPacket) );
    _outLen     = 0;
    _outSent    = 0;
    _inRecv     = 0;
    _inExpected = 0;
    _inActual   = 0;
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
        0x91af,  0xa7f1,  0xfd13,  0xcb4d,  0x48d7,  0x7e89,  0x246b,  0x1235};
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


int CtiDNPDatalink::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    switch( _ioState )
    {
        case Output:
            {
                xfer.setOutBuffer((unsigned char *)&_outPacket);
                xfer.setOutCount(_outLen);
                xfer.setCRCFlag(0);

                //  ACH: this will need to be changed when secondary ACK/NACK packets are included,
                //    but for now we ignore any incoming until we're done sending
                xfer.setInBuffer((unsigned char *)&_inPacket);
                xfer.setInCountExpected(0);
                xfer.setInCountActual(&_inActual);
                xfer.setNonBlockingReads(false);

                break;
            }

        case Input:
            {
                //  ACH: someday will need to ACK or NACK previous packet...
                xfer.setOutBuffer((unsigned char *)&_outPacket);
                xfer.setOutCount(0);
                xfer.setCRCFlag(0);

                xfer.setInBuffer((unsigned char *)&_inPacket);
                xfer.setInCountExpected(_inExpected);    //  max frame size, but...
                xfer.setInCountActual(&_inActual);
                xfer.setNonBlockingReads(true);  //    ...  take what we can get

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

    return retVal;
}


int CtiDNPDatalink::decode( CtiXfer &xfer, int status )
{
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
    }
    else
    {
        switch( _ioState )
        {
            case Output:
                {
                    _outSent += xfer.getOutCount();
                    //  someday receive ACK packet
                    break;
                }

            case Input:
                {
                    dst   = (unsigned char *)(&_inPacket);
                    dst  += _inRecv;

                    src    = xfer.getInBuffer();
                    //  previously, "xfer.setInCountActual(&_inActual);", so this is valid
                    srcLen = _inActual;

                    //  special case - we make sure we have the header before we decide on packet length
                    if( _inRecv < DNPDatalinkHeaderLen )
                    {
                        if( (_inRecv + srcLen) >  DNPDatalinkHeaderLen )
                        {
                            toCopy = DNPDatalinkHeaderLen - _inRecv;
                        }
                        else
                        {
                            toCopy = srcLen;
                        }

                        memcpy( dst, src, toCopy );

                        src     += toCopy;
                        srcLen  -= toCopy;
                        _inRecv += toCopy;
                    }

                    //  make sure we read something - the bytes may have been consumed by the above block
                    if( srcLen > 0 )
                    {
                        int packetLength, dataLength, numBlocks;

                        //  subtract off the other header bytes for data length calculation
                        dataLength = _inPacket.header.len - 5;

                        numBlocks  =  dataLength / 16;
                        numBlocks += (dataLength % 16)?1:0;

                        packetLength  = DNPDatalinkHeaderLen;
                        packetLength += dataLength;
                        packetLength += numBlocks * 2;  //  add on the CRC bytes

                        //  if we have more data than we know what to do with, don't cause a buffer overflow...
                        if( (srcLen + DNPDatalinkHeaderLen) > packetLength )
                        {
                            toCopy = packetLength - _inRecv;

                            //  ACH: what to do with extraneous bytes...  ?  they're part of the next packet
                            //    hoping for now that this never happens
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                        else
                        {
                            toCopy = srcLen;
                        }

                        memcpy( dst, src, toCopy );

                        _inRecv += toCopy;
                    }
                }

            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
        }
    }

    return 0;
}


int CtiDNPDatalink::getInLength(void)
{
    return _inPacket.header.len;
}


int CtiDNPDatalink::getInPayload(unsigned char *buf)
{
    //  remove CRCs, jam data together, yo
    int retVal = NoError;

    int msgLen, copied, toCopy;
    unsigned char *msg;

    //  subtract the header length
    msgLen = _inPacket.header.len - 5;

    msg = new unsigned char[msgLen];

    if( msg != NULL )
    {
        copied = 0;

        while( copied < msgLen )
        {
            toCopy = msgLen - copied;

            if( toCopy > 16 )
                toCopy = 16;

            memcpy(&msg[copied], _inPacket.data.blocks[copied/16], toCopy);

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

    return retVal;
}


int CtiDNPDatalink::getOutLength( void )
{
    return _outLen;
}


int CtiDNPDatalink::setToOutput(unsigned char *buf, unsigned int len, short dstAddr, short srcAddr)
{
    //  insert CRCs, etc etc

    int pos, blockLen;
    unsigned short crc, *crcPos;

    _ioState = Output;

    pos = 0;

    memset((void *)&_outPacket, 0, sizeof(_outPacket));

    //  if it's too big or there's nothing to copy, set our buffer to zip
    if( len > 250 || buf == NULL )
        len = 0;

    _outLen = 10;  //  minimum DNP packet size (just header and header CRC)

    while( pos < len )
    {
        blockLen = len - pos;

        if( blockLen > 16 )
            blockLen = 16;

        //  copy in this block of data
        memcpy(_outPacket.data.blocks[pos/16], &buf[pos], blockLen);

        //  figure out where the CRC should go
        crcPos = (unsigned short *)(_outPacket.data.blocks[pos/16] + blockLen);

        //  tack on the CRC
        *crcPos = computeCRC(_outPacket.data.blocks[pos/16], blockLen);

        pos += blockLen;

        _outLen += blockLen + 2;  //  add on block and CRC lengths
    }

    _outPacket.header.framing[0] = 0x05;
    _outPacket.header.framing[1] = 0x64;

    //  add on the header length
    _outPacket.header.len = len + 5;

    _outPacket.header.destination = dstAddr;
    _outPacket.header.source      = srcAddr;

    _outPacket.header.control.p.direction = 1;
    _outPacket.header.control.p.primary   = 1;
    _outPacket.header.control.p.fcv = 0;
    _outPacket.header.control.p.fcb = 0;
    _outPacket.header.control.p.functionCode = 4;

    //  tack on the CRC
    _outPacket.header.crc = computeCRC((unsigned char *)&_outPacket.header, 8);

    return pos;
}


int CtiDNPDatalink::setToInput( void )
{
    int retVal = NoError;

    _ioState = Input;

    memset((void *)&_inPacket,  0, sizeof(_inPacket) );

    _inRecv     = 0;
    _inExpected = 292;
    _inActual   = 0;

    return retVal;
}


bool CtiDNPDatalink::isTransactionComplete( void )
{
    bool complete = false;

    switch( _ioState )
    {
        case Output:
            {
                //  ACH: modify to wait for inbound ACK when secondary enabled
                if( _outLen == _outSent )
                {
                    complete = true;
                }

                break;
            }

        case Input:
            {
                if( _inRecv > DNPDatalinkHeaderLen )
                {
                    int packetLength, dataLength, numBlocks;

                    //  subtract off the other header bytes, they're already included
                    dataLength = (_inPacket.header.len - 5);

                    numBlocks  =  dataLength / 16;
                    numBlocks += (dataLength % 16)?1:0;

                    packetLength  = DNPDatalinkHeaderLen;
                    packetLength += dataLength;
                    packetLength += numBlocks * 2;  //  add on the CRC bytes

                    if( _inRecv == packetLength )
                    {
                        complete = true;
                    }
                }

                break;
            }

        default:
            {
                //  if we're uninitialized, we had nothing to do - so we're finished
                complete = true;
                break;
            }
    }

    return complete;
}


bool CtiDNPDatalink::areInPacketCRCsValid( void )
{
    bool valid = true;

    int blockLen, pos, len;
    unsigned short crc, blockCRC;

    //  compute the header's CRC
    crc = computeCRC((unsigned char *)(&_inPacket.header.control), 5);

    if( crc != _inPacket.header.crc )
    {
        valid = false;
    }
    else
    {
        pos = 0;
        len = _inPacket.header.len - 5;

        while( pos < len && valid )
        {
            blockLen = len - pos;

            if( blockLen > 16 )
                blockLen = 16;

            //  copy in this block of data
            crc = computeCRC(_inPacket.data.blocks[pos/16], blockLen);

            //  snag the CRC from the end of the block
            blockCRC = *((unsigned short *)(&_inPacket.data.blocks[pos/16][blockLen]));

            pos += blockLen;

            //  compare the CRCs
            if( crc != blockCRC )
            {
                valid = false;
            }
        }
    }

    return valid;
}


CtiDNPDatalink::DatalinkError CtiDNPDatalink::validateInPacket( void )
{
    DatalinkError retVal = NoError;

    if( _inPacket.header.framing[0] != 0x05 ||
        _inPacket.header.framing[1] != 0x64 )
    {
        retVal = BadFraming;
    }
    else if( !areInPacketCRCsValid() )
    {
        retVal = BadCRC;
    }

    return retVal;
}

