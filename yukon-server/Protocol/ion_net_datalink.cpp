/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_datalink.cpp
 *
 * Classes: CtiIONDatalinkLayer
 * Date:    2002-oct-02
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctitypes.h"
#include "guard.h"
#include "logger.h"

#include "ion_net_datalink.h"

#include "numstr.h"

CtiIONDatalinkLayer::CtiIONDatalinkLayer( )
{
    _ioState    = Uninitialized;
    _data       = NULL;
}

CtiIONDatalinkLayer::~CtiIONDatalinkLayer( )
{
    freeMemory( );
}


void CtiIONDatalinkLayer::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _src = srcID;
    _dst = dstID;
}


bool CtiIONDatalinkLayer::isTransactionComplete( void )
{
    return _ioState == Complete;
}


bool CtiIONDatalinkLayer::errorCondition( void )
{
    return _ioState == Failed;
}


void CtiIONDatalinkLayer::setToOutput( CtiIONSerializable &payload )
{
    freeMemory( );

    _dataSent       = 0;

    _ioState        = Output;

    _commErrorCount     = 0;
    _protocolErrorCount = 0;

    _dataLength = payload.getSerializedLength( );
    _data       = CTIDBG_new unsigned char[_dataLength];

    if( _data != NULL )
    {
        payload.putSerialized( _data );
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << _dataLength << " bytes in CtiIONDatalinkLayer ctor;"
                                                                 << "  setting zero data length, _ioState = Failed" << endl;
        _dataLength = 0;
        _ioState    = Failed;
    }
}


void CtiIONDatalinkLayer::setToInput( void )
{
    freeMemory( );

    _dataLength         = 0;
    _data               = NULL;

    _currentInputFrame  = -1;
    _inTotal            =  0;

    _ioState            = InputHeader;

    _commErrorCount     = 0;
    _protocolErrorCount = 0;
}


void CtiIONDatalinkLayer::freeMemory( void )
{
    int i;

    while( !_inputFrameVector.empty( ) )
    {
        //  delete all new'd instances
        delete _inputFrameVector.back();

        _inputFrameVector.pop_back();
    }

    if( _data != NULL )
    {
        delete [] _data;
        _data = NULL;
    }
}


void CtiIONDatalinkLayer::putPayload( unsigned char *buf )
{
    int i, offset, dataLen;

    offset = 0;

    //  if this function is called during output mode, the payload will be empty and nothing will be copied.

    for( i = 0; i < _inputFrameVector.size( ); i++ )
    {
        dataLen = (_inputFrameVector[i])->header.len - EmptyPacketLength;

        memcpy( (buf + offset), (_inputFrameVector[i])->data, dataLen );

        offset += dataLen;
    }
}


int CtiIONDatalinkLayer::getPayloadLength( void )
{
    int i, payloadLength = 0;

    for( i = 0; i < _inputFrameVector.size( ); i++ )
    {
        payloadLength += (_inputFrameVector[i])->header.len - EmptyPacketLength;
    }

    return payloadLength;
}


int CtiIONDatalinkLayer::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    _inActual = 0;

    xfer.setInTimeout(0);

    switch( _ioState )
    {
        case InputHeader:
        {
            xfer.setOutBuffer((unsigned char *)&_outFrame);  //  placeholder, but it's nicer than passing NULL to the function
            xfer.setOutCount(0);
            xfer.setCRCFlag(0);

            xfer.setInBuffer(_inBuffer);
            xfer.setInCountExpected(EmptyPacketLength + UncountedHeaderBytes - _inTotal);
            xfer.setInCountActual(&_inActual);
            break;
        }

        case InputPacket:
        {
            xfer.setOutBuffer((unsigned char *)&_outFrame);  //  placeholder, but it's nicer than passing NULL to the function
            xfer.setOutCount(0);
            xfer.setCRCFlag(0);

            xfer.setInBuffer(((unsigned char *)&_inFrame) + _inTotal);
            xfer.setInCountExpected(_inFrame.header.len + UncountedHeaderBytes - _inTotal);
            xfer.setInCountActual(&_inActual);
            break;
        }

        case InputSendAck:
        {
            retVal = generateAck(&_outFrame);

            xfer.setOutBuffer((unsigned char *)&_outFrame);
            xfer.setOutCount(EmptyPacketLength + UncountedHeaderBytes);
            xfer.setCRCFlag(0);

            xfer.setInBuffer((unsigned char *)&_inFrame);
            xfer.setInCountExpected(0);
            xfer.setInCountActual(&_inActual);
            break;
        }

        case InputSendNack:
        {
            retVal = generateNack(&_outFrame);

            xfer.setOutBuffer((unsigned char *)&_outFrame);
            xfer.setOutCount(EmptyPacketLength + UncountedHeaderBytes);
            xfer.setCRCFlag(0);

            xfer.setInBuffer((unsigned char *)&_inFrame);
            xfer.setInCountExpected(0);
            xfer.setInCountActual(&_inActual);
            break;
        }

        case Output:
        {
            retVal = generateOutputFrame(&_outFrame);

            xfer.setOutBuffer((unsigned char *)&_outFrame);
            xfer.setOutCount(_outFrame.header.len + UncountedHeaderBytes);
            xfer.setCRCFlag(0);

            xfer.setInBuffer((unsigned char *)&_inBuffer);

            //  expect an ACK if DataAcknakEnbl is set
            if( _outFrame.header.cntlframetype == DataAcknakEnbl )
            {
                xfer.setInCountExpected(EmptyPacketLength + UncountedHeaderBytes);

                _ioState = OutputRecvAckNack;
            }
            else
            {
                xfer.setInCountExpected(0);
            }

            xfer.setInCountActual(&_inActual);

            break;
        }

        case OutputRecvAckNack:
        {
            xfer.setOutBuffer((unsigned char *)&_outFrame);  //  placeholder, but it's nicer than passing NULL to the function
            xfer.setOutCount(0);
            xfer.setCRCFlag(0);

            xfer.setInBuffer(((unsigned char *)&_inBuffer) + _inTotal);
            xfer.setInCountExpected(EmptyPacketLength + UncountedHeaderBytes - _inTotal);
            xfer.setInCountActual(&_inActual);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _ioState = Failed;

            break;
        }
    }

#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        if( xfer.getOutCount() > 0 )
        {
            dout << "Datalink layer output (" << xfer.getOutCount() << " bytes)" << endl;
            for( int i = 0; i < xfer.getOutCount(); i++ )
            {
                dout << CtiNumStr((xfer.getOutBuffer())[i]).hex().zpad(2) << " ";
            }
            dout << endl;
        }
    }
#endif

    return retVal;
}


int CtiIONDatalinkLayer::generateOutputFrame( ion_frame *frame )
{
    int retVal = 0;
    int payloadLen;

    frame->header.reserved     = 0;
    frame->header.cntlreserved = 0;
    frame->header.srcreserved  = 0;
    frame->header.tranreserved = 0;

    frame->header.sync = 0x14;
    frame->header.fmt  = 0xAC;

    frame->header.srcid = _src;
    frame->header.dstid = _dst;

    frame->header.cntldirection = 0;
    frame->header.cntlframetype = DataAcknakEnbl;

    payloadLen = _dataLength - _dataSent;

    if( payloadLen > MaxPayloadLength )
    {
        payloadLen = MaxPayloadLength;
    }

    if( _dataSent == 0 )
    {
        //  number of full frames
        _currentOutputFrame = _dataLength / MaxPayloadLength;

        //  plus a partial frame, if need be
        if( _dataLength % MaxPayloadLength )
        {
            _currentOutputFrame++;
        }

        //  then subtract 1 to make it zero-based
        _currentOutputFrame--;

        frame->header.tranfirstframe = 1;
    }
    else
    {
        frame->header.tranfirstframe = 0;
    }

    frame->header.trancounter = _currentOutputFrame;

    //  copy the struct over into the char buffer
    memcpy(frame->data, _data + _dataSent, payloadLen );

    frame->header.len = EmptyPacketLength + payloadLen;

    setCRC(frame);

    _bytesInLastFrame = payloadLen;

    //  set to 0 in anticipation of an ACK packet, if we expect one
    _inTotal = 0;

    return retVal;
}


int CtiIONDatalinkLayer::generateAck( ion_frame *frame )
{
    int retVal = 0;

    frame->header.reserved     = 0;
    frame->header.cntlreserved = 0;
    frame->header.srcreserved  = 0;
    frame->header.tranreserved = 0;

    frame->header.sync = 0x14;
    frame->header.fmt  = 0xAC;

    frame->header.srcid = _src;
    frame->header.dstid = _dst;

    frame->header.cntldirection = 0;
    frame->header.cntlframetype = AcknakACK;

    frame->header.trancounter = _inFrame.header.trancounter;

    frame->header.len = EmptyPacketLength;

    setCRC(frame);

    return retVal;
}


int CtiIONDatalinkLayer::generateNack( ion_frame *frame )
{
    int retVal = 0;

    frame->header.reserved     = 0;
    frame->header.cntlreserved = 0;
    frame->header.srcreserved  = 0;
    frame->header.tranreserved = 0;

    frame->header.sync = 0x14;
    frame->header.fmt  = 0xAC;

    frame->header.srcid = _src;
    frame->header.dstid = _dst;

    frame->header.cntldirection = 0;
    frame->header.cntlframetype = AcknakNAK;

    frame->header.trancounter = _inFrame.header.trancounter;

    frame->header.len = EmptyPacketLength;

    setCRC(frame);

    return retVal;
}


int CtiIONDatalinkLayer::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;
    int offset;

    ion_frame *saveFrame;

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

        if( ++_commErrorCount >= CommRetries )
        {
            _ioState = Failed;
            retVal   = status;
        }
    }
    else
    {

        #if 0
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            if( *(xfer.getInCountActual()) > 0 )
            {
                dout << "Datalink layer input (" << *(xfer.getInCountActual()) << " bytes)" << endl;
                for( int i = 0; i < *(xfer.getInCountActual()); i++ )
                {
                    dout << CtiNumStr((xfer.getInBuffer())[i]).hex().zpad(2) << " ";
                }
                dout << endl;
            }
            else
            {
                dout << "No datalink layer input" << endl;
            }
        }
        #endif

        switch( _ioState )
        {
            case InputHeader:
            {
                if( _inTotal == 0 )
                {
                    //  still looking for the sync byte
                    offset = 0;

                    while( *(_inBuffer + offset) != 0x14 && offset < _inActual )
                    {
                        offset++;
                    }

                    if( offset < _inActual )
                    {
                        memcpy(((unsigned char *)&_inFrame), _inBuffer + offset, _inActual - offset);

                        _inTotal += (_inActual - offset);
                    }
                }
                else
                {
                    //  got the sync byte already, just copying header now
                    memcpy(((unsigned char *)&_inFrame) + _inTotal, _inBuffer, _inActual);

                    _inTotal += _inActual;

                    if( _inTotal >= (EmptyPacketLength + UncountedHeaderBytes) )
                    {
                        _ioState = InputPacket;
                    }
                }

                break;
            }

            case InputPacket:
            {
                _inTotal += _inActual;

                if( _inTotal >= _inFrame.header.len + UncountedHeaderBytes )
                {
                    if( crcIsValid(&_inFrame) )
                    {
                        //  ACH:  do _src and _dst need checking?

                        //  if it claims to be the first frame and we're expecting the first frame
                        if( _inFrame.header.tranfirstframe && _currentInputFrame < 0 )
                        {
                            //  frame count to expect
                            _currentInputFrame = _inFrame.header.trancounter;
                        }

                        if( _currentInputFrame == _inFrame.header.trancounter )
                        {
                            saveFrame = new ion_frame;

                            if( saveFrame != NULL )
                            {
                                *saveFrame = _inFrame;

                                //  put the frame in the list
                                _inputFrameVector.push_back( saveFrame );

                                if( _inFrame.header.cntlframetype == DataAcknakEnbl )
                                {
                                    //  we're ready to send an ACK
                                    _ioState = InputSendAck;
                                }
                                else
                                {
                                    if( _currentInputFrame == 0 )
                                    {
                                        _ioState = Complete;
                                    }
                                    else
                                    {
                                        _ioState = InputHeader;
                                    }
                                }
                            }
                            else
                            {
                                _ioState = Failed;
                            }
                        }
                        else
                        {
                            _protocolErrorCount++;

                            if( _inFrame.header.cntlframetype == DataAcknakEnbl )
                            {
                                //  send a NACK
                                _ioState = InputSendNack;
                            }
                            else
                            {
                                _ioState = InputHeader;
                            }
                        }
                    }
                    else
                    {
                        _protocolErrorCount++;

                        //  i have no way to accurately tell if they said this was going to be an ACK-enabled packet,
                        //    what with the mangled data, so the best i can do is just try to read again
                        _ioState = InputHeader;
                    }
                }

                break;
            }

            case InputSendAck:
            case InputSendNack:
            {
                if( _currentInputFrame == 0 )
                {
                    _ioState = Complete;
                }
                else
                {
                    _ioState = InputHeader;
                }

                break;
            }

            case Output:
            {
                _ioState = Complete;

                break;
            }

            case OutputRecvAckNack:
            {
                //  ADD CODE HERE:  do SRC and DST need checking?

                if( _inTotal == 0 )
                {
                    //  still looking for the sync byte
                    offset = 0;

                    while( *(_inBuffer + offset) != 0x14 && offset < _inActual )
                    {
                        offset++;
                    }

                    if( offset < _inActual )
                    {
                        memcpy(((unsigned char *)&_inFrame), _inBuffer + offset, _inActual - offset);

                        _inTotal += (_inActual - offset);
                    }
                    else
                    {
                        _protocolErrorCount++;
                    }
                }
                else
                {
                    //  got the sync byte already, just copying header now
                    memcpy(((unsigned char *)&_inFrame) + _inTotal, _inBuffer, _inActual);

                    _inTotal += _inActual;
                }

                if( _inTotal >= (EmptyPacketLength + UncountedHeaderBytes) )
                {
                    if( _inFrame.header.cntlframetype == AcknakACK &&   //  make sure it's an ACK frame
                        _inFrame.header.trancounter == _currentOutputFrame )          //  make sure they're ACKing the frame we just sent
                    {
                        _dataSent += _bytesInLastFrame;

                        if( _currentOutputFrame == 0 )
                        {
                            _ioState = Complete;
                        }
                        else
                        {
                            _currentOutputFrame--;
                            _ioState = Output;
                        }
                    }
                    else
                    {
                        _protocolErrorCount++;
                        _ioState = Output;
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

                _ioState = Failed;

                break;
            }
        }

        if( _protocolErrorCount > ProtocolRetries )
        {
            _ioState = Failed;
        }
    }

    return retVal;
}



void CtiIONDatalinkLayer::setCRC( ion_frame *frame )
{
    unsigned int frameCRC;
    int dataLen;

    dataLen  = frame->header.len - EmptyPacketLength;

    frameCRC = crc16( frame->data - PrePayloadCRCOffset, dataLen + PrePayloadCRCOffset );  //  CRC is computed on the data plus the 8 bytes preceding

    frame->data[dataLen]   =  frameCRC & 0x00FF;        //  the bytes right after the data ends
    frame->data[dataLen+1] = (frameCRC & 0xFF00) >> 8;  //
}


bool CtiIONDatalinkLayer::crcIsValid( ion_frame *frame )
{
    unsigned int frameCRC, computedCRC;
    int dataLen;

    dataLen = frame->header.len - EmptyPacketLength;  //  len = data length + 7


    frameCRC  = frame->data[dataLen];         //  the bytes right after the data ends
    frameCRC += frame->data[dataLen+1] << 8;  //

    computedCRC = crc16( frame->data - PrePayloadCRCOffset, dataLen + PrePayloadCRCOffset );  //  CRC is computed on the data plus the 8 bytes preceding

    return frameCRC == computedCRC;
}


unsigned int CtiIONDatalinkLayer::crc16( unsigned char *data, int length )
{
    //  CRC-16 computation
    //    from http://www.programmingparadise.com/vs/?crc/crcfast.c.html
    //    original author unknown, so i figured it was okay to use.

    unsigned short tmp, crc;

    unsigned short crc16table[256] =
    {
        0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
        0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
        0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
        0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
        0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
        0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
    };

    crc = 0xFFFF;

    for( int i = 0; i < length; i++ )
    {
        tmp = crc ^ (unsigned short)data[i];
        crc = (crc >> 8) ^ crc16table[tmp & 0x00FF];
    }

    return crc;
}

