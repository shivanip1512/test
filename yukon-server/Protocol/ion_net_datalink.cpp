/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_datalink.cpp
 *
 * Classes: CtiIONDataLinkLayer, CtiIONFrame
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


CtiIONDataLinkLayer::CtiIONDataLinkLayer( )
{
    _valid  = false;
    _status = Uninitialized;
    _data       = NULL;
    _tmpIOFrame = NULL;
}

CtiIONDataLinkLayer::~CtiIONDataLinkLayer( )
{
    freeMemory( );
}


void CtiIONDataLinkLayer::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _src = srcID;
    _dst = dstID;
}


CtiIONDataLinkLayer::DLLExternalStatus CtiIONDataLinkLayer::getStatus( void )
{
    return _status;
}

bool CtiIONDataLinkLayer::isValid( void )
{
    return _valid;
}


void CtiIONDataLinkLayer::setToOutput( CtiIONSerializable &payload )
{
    freeMemory( );

    _valid = TRUE;

    _dataSent  = 0;
    _status    = OutDataReady;
    _direction = Output;
    _retries   = IONRetries;

    _dataLength = payload.getSerializedLength( );
    _data = CTIDBG_new unsigned char[_dataLength];

    if( _data != NULL )
    {
        payload.putSerialized( _data );
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << _dataLength << " bytes in CtiIONDataLinkLayer ctor;"
                                                                 << "  setting zero data length, valid = FALSE, status = Abort" << endl;
        _dataLength = 0;
        _valid = FALSE;
        _status = Abort;
    }
}


void CtiIONDataLinkLayer::setToInput( void )
{
    freeMemory( );

    _valid = TRUE;

    _dataLength = 0;
    _data       = NULL;
    _currentFrame = -1;
    _status     = InDataReady;
    _direction = Input;
    _retries   = IONRetries;
}


void CtiIONDataLinkLayer::freeMemory( void )
{
    int i;

    while( !_inputFrameVector.empty( ) )
    {
        //  delete all CTIDBG_new'd instances
        delete _inputFrameVector.back();

        _inputFrameVector.pop_back();
    }

    if( _tmpIOFrame != NULL )
    {
        delete _tmpIOFrame;
    }

    if( _data != NULL )
    {
        delete [] _data;
        _data = NULL;
    }
}


void CtiIONDataLinkLayer::putPayload( unsigned char *buf )
{
    int i, offset;

    offset = 0;

    //  if this function is called during output mode, the payload will be empty and nothing will be copied.

    for( i = 0; i < _inputFrameVector.size( ); i++ )
    {
        _inputFrameVector[i]->putPayload( buf + offset );
        offset += _inputFrameVector[i]->getPayloadLength( );
    }
}


int CtiIONDataLinkLayer::getPayloadLength( void )
{
    int i, payloadLength = 0;

    for( i = 0; i < _inputFrameVector.size( ); i++ )
    {
        payloadLength += _inputFrameVector[i]->getPayloadLength( );
    }

    return payloadLength;
}


int CtiIONDataLinkLayer::inFrame( unsigned char *data, unsigned long dataLength )
{
    CtiIONFrame *tmpFrame;

    tmpFrame = CTIDBG_new CtiIONFrame();

    if( tmpFrame != NULL )
    {
        tmpFrame->initInputFrame( data, dataLength );

        if( tmpFrame->crcIsValid( ) )
        {
            switch( _direction )
            {
                case Input:
                {
                    //  ADD CODE HERE:  do SRC and DST need checking?

                    //  if claims to be the first frame and we're expecting the first frame
                    if( tmpFrame->isFirstFrame( ) && _currentFrame < 0 )
                    {
                        //  frame count to expect
                        _currentFrame = tmpFrame->getCounter( );
                        //  put the frame in the list
                        _inputFrameVector.push_back( tmpFrame );
                        //  we're ready to send an ACK
                        _status = OutAckReady;
                    }
                    //  if it's the right frame
                    else if( _currentFrame == tmpFrame->getCounter( ) )
                    {
                        //  put the frame in the list
                        _inputFrameVector.push_back( tmpFrame );
                        //  we're ready to send an ACK
                        _status = OutAckReady;
                    }
                    else
                    {
                        _retries--;
                        _status = OutNakReady;
                        delete tmpFrame;
                    }
                    break;
                }

                case Output:
                {
                    //  ADD CODE HERE:  do SRC and DST need checking?

                    if( tmpFrame->getFrameType( ) == CtiIONFrame::AcknakACK &&  //  make sure it's an ACK frame
                        tmpFrame->getCounter( ) == _currentFrame )          //  make sure they're ACKing the frame we just sent
                    {
                        _dataSent += _bytesInLastFrame;
                        if( _currentFrame == 0 )
                        {
                            _status = OutDataComplete;
                        }
                        else
                        {
                            _currentFrame--;
                            _status = OutDataReady;
                        }
                    }
                    else
                    {
                        _retries--;
                        _status = OutDataRetry;
                    }

                    delete tmpFrame;
                }
            }
        }
        else
        {
            _retries--;
            _status = OutNakReady;
            delete tmpFrame;
        }

        if( _retries < 0 )
        {
            _status = Abort;
        }
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << sizeof( CtiIONFrame ) << " bytes in CtiIONDataLinkLayer inFrame;"
                                                                 << "  setting status = Abort" << endl;
        _status = Abort;
    }

    return _status;
}


CtiIONFrame *CtiIONDataLinkLayer::outFrame( void )
{
    int bytesInNewFrame, numFrames, tmpCRC;

    CtiIONFrame *tmpFrame;

    //  all fields in here are set as master-oriented parameters (always setting frame to be
    //    from master to slave, etc)

    tmpFrame = CTIDBG_new CtiIONFrame();

    if( tmpFrame != NULL )
    {
        tmpFrame->initOutputFrame();

        switch( _direction )
        {
            case Output:
            {
                bytesInNewFrame = _dataLength - _dataSent;

                if( bytesInNewFrame > CtiIONFrame::MaxPayloadLength )
                {
                    bytesInNewFrame = CtiIONFrame::MaxPayloadLength;
                }

                if( _dataSent == 0 )
                {
                    //  number of full frames
                    _currentFrame = _dataLength / CtiIONFrame::MaxPayloadLength;
                    //  plus a partial frame, if need be
                    if( _dataLength % CtiIONFrame::MaxPayloadLength )
                        _currentFrame++;
                    //  then subtract 1 to make it zero-based
                    _currentFrame--;

                    tmpFrame->setFirstFrame( TRUE );
                }

                //  copy the struct over into the char buffer
                tmpFrame->setPayload( _data + _dataSent, bytesInNewFrame );

                tmpFrame->setFrameType( CtiIONFrame::DataAcknakEnbl );

                tmpFrame->setSrcID( _src );
                tmpFrame->setDstID( _dst );

                tmpFrame->setCounter( _currentFrame );

                tmpFrame->setCRC( );

                _bytesInLastFrame = bytesInNewFrame;

                break;
            }

            case Input:
            {
                switch( _status )
                {
                    case OutAckReady:
                    case OutAckRetry:
                        tmpFrame->setFrameType( CtiIONFrame::AcknakACK );
                        break;

                    case OutNakReady:
                        tmpFrame->setFrameType( CtiIONFrame::AcknakNAK );
                        break;
                }

                tmpFrame->setPayload( _data, 0 );

                tmpFrame->setSrcID( _src );
                tmpFrame->setDstID( _dst );

                tmpFrame->setCounter( _currentFrame );

                tmpFrame->setCRC( );

                break;
            }
        }
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << sizeof( CtiIONFrame ) << " bytes in CtiIONDataLinkLayer inFrame" << endl;
        _status = Abort;
    }

    return tmpFrame;
}


int CtiIONDataLinkLayer::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    CtiIONFrame *tmpOutFrame;

    tmpOutFrame = outFrame();

    if( tmpOutFrame != NULL )
    {
        tmpOutFrame->putSerialized(_outBuffer);

        xfer.setOutBuffer(_outBuffer);

        xfer.setOutCount(tmpOutFrame->getSerializedLength());
        xfer.setCRCFlag(0);

        //  ACH: this will need to be changed when secondary ACK/NACK packets are included,
        //    but for now we ignore any incoming until we're done sending
        //  (er?  is this correct?)
        xfer.setInBuffer(_inBuffer);
        xfer.setInCountExpected(0);
        xfer.setInCountActual(&_inActual);
        xfer.setNonBlockingReads(false);

        xfer.setInCountExpected(CtiIONFrame::EmptyPacketLength);
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


int CtiIONDataLinkLayer::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;

    return inFrame(_inBuffer, *(xfer.getInCountActual()));
}



CtiIONFrame::CtiIONFrame( )     {   }

CtiIONFrame::~CtiIONFrame( )    {   }


CtiIONFrame::initOutputFrame( )
{
    initReserved( );
    _frame.header.sync = 0x14;  //  start of data
    _frame.header.fmt  = 0xAC;  //  format = ION frame
    _frame.header.len  = EmptyPacketLength;
}


CtiIONFrame::initInputFrame( unsigned char *rawFrame, int rawFrameLength )
{
    //  only copy what we have room for...
    if( rawFrameLength > MaxFrameLength )
        rawFrameLength = MaxFrameLength;

    memcpy( &_frame, rawFrame, rawFrameLength );
}


void CtiIONFrame::initReserved( void )
{
    _frame.header.cntlreserved  = 0;
    _frame.header.cntldirection = 0;  //  we're the master
    _frame.header.srcreserved   = 0;
    _frame.header.tranreserved  = 0;
    _frame.header.reserved      = 0;
}



void CtiIONFrame::putSerialized( unsigned char *buf ) const
{
    memcpy( buf, &_frame, getSerializedLength( ) );
}


unsigned int CtiIONFrame::getSerializedLength( void ) const
{
    return _frame.header.len + UncountedHeaderBytes;
}


void CtiIONFrame::setPayload( unsigned char *buf, int len )
{
    if( len )
    {
        if( len > MaxPayloadLength )
            len = MaxPayloadLength;

        memcpy( _frame.data, buf, len );
    }

    _frame.header.len = len + EmptyPacketLength;
}


void CtiIONFrame::putPayload( unsigned char *buf )
{
    memcpy( _frame.data, buf, getPayloadLength( ) );
}


void CtiIONFrame::setFrameType( FrameType frameType )
{
    _frame.header.cntlframetype = frameType;
}


void CtiIONFrame::setCRC( void )
{
    unsigned int frameCRC;
    int dataLen;

    dataLen = _frame.header.len - EmptyPacketLength;

    frameCRC = crc16( _frame.data - PrePayloadCRCOffset, dataLen + PrePayloadCRCOffset );  //  CRC is computed on the data plus the 8 bytes preceding

    _frame.data[dataLen]   =  frameCRC & 0x00FF;        //  the bytes right after the data ends
    _frame.data[dataLen+1] = (frameCRC & 0xFF00) >> 8;  //
}


int CtiIONFrame::crcIsValid( void )
{
    unsigned int frameCRC, computedCRC;
    int dataLen;

    dataLen = _frame.header.len - EmptyPacketLength;  //  len = data length + 7


    frameCRC  = _frame.data[dataLen];         //  the bytes right after the data ends
    frameCRC += _frame.data[dataLen+1] << 8;  //

    computedCRC = crc16( _frame.data - PrePayloadCRCOffset, dataLen + PrePayloadCRCOffset );  //  CRC is computed on the data plus the 8 bytes preceding

    return frameCRC == computedCRC;
}


unsigned int CtiIONFrame::crc16( unsigned char *data, int length )
{
    //  CRC-16 computation
    //    from http://www.programmingparadise.com/vs/?crc/crcfast.c.html
    //    original author unknown, so i figured it was okay to use.

    unsigned short tmp,
                   crc = 0xffff;

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

    for( int i = 0; i < length; i++ )
    {
        tmp = crc ^ (unsigned short)data[i];
        crc = (crc >> 8) ^ crc16table[tmp & 0x00FF];
    }

    return crc;
}

