/*-----------------------------------------------------------------------------*
 *
 * File:    ion_netlayers.cpp
 *
 * Classes: CtiIONDataStream, CtiIONApplicationLayer, CtiIONNetworkLayer, 
 *            CtiIONDataLinkLayer
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ion_netlayers.h"
#include "ctitypes.h"
#include "guard.h"
#include "logger.h"

void CtiIONApplicationLayer::init( CtiIONDataStream &dataStream )
{
    int            itemNum, 
                   dataOffset,
                   dataLength;
    unsigned char *tmpData;

    
    freeMemory( );
    
    _valid = TRUE;
    initReserved( );

    dataLength = dataStream.getSerializedLength( );
    
    //  fill up the header
    _alData.header.service = 0x0F;  //  start, execute, and end the program in a single request
    //  _applicationMessage.status = -1;      //  no status byte in requests (which are all we, the master, send via the application layer)
    _alData.header.pid = 1;            //  can be set to anything - only one program/PID per request
    _alData.header.freq = 1;           //  programs can currently only be executed once
    _alData.header.priority = 0;       //  only support default priority
    _alData.header.length.byte1 = (dataLength & 0xFF00) >> 8;
    _alData.header.length.byte0 =  dataLength & 0x00FF;

    //  copy in the serialized datastream values
    _alData.IONData = new unsigned char[dataLength];
    
    if( _alData.IONData != NULL )
    {
        dataStream.putSerialized( _alData.IONData );
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << dataLength << " bytes in CtiIONApplicationLayer ctor;"
                                                                 << "  proceeding with zero-length ION data payload, setting valid = FALSE" << endl;
        _alData.header.length.byte1 = 0;
        _alData.header.length.byte0 = 0;
        _valid = FALSE;
    }
}


void CtiIONApplicationLayer::init( CtiIONNetworkLayer &netLayer )
{
    int            alHeaderSize, 
                   tmpSize, 
                   tmpIONDataSize;
    unsigned char *tmpData, 
                  *headerDataPos;
    
    
    freeMemory( );
    
    _valid = TRUE;
    initReserved( );

    alHeaderSize = sizeof( _alData.header );
    tmpSize = netLayer.getPayloadLength( );
    
    tmpData = new unsigned char[tmpSize];
    
    if( tmpData != NULL )
    {
        netLayer.putPayload( tmpData );

        //  this is used to keep track of where we're copying the data to in the header
        headerDataPos = (unsigned char *)&(_alData.header);  


        //  --------
        //  copy the header
        //  --------

        //  copy the service bytes
        memcpy( headerDataPos, tmpData, 2 );
        headerDataPos += 2;

        //  if it's a request message, it doesn't have the status byte in the header
        if( isRequest( ) )
        {
            //  reduce the header size to reflect the missing status byte 
            //    (this is used as the amount left to copy)
            alHeaderSize--;
            //  and offset past the status byte in the destination
            headerDataPos += 1;
        }

        //  copy the rest of the header into the relevant spot in memory
        memcpy( headerDataPos, tmpData + 2, alHeaderSize - 2 );


        //  --------
        //  copy the data
        //  --------

        tmpIONDataSize  = _alData.header.length.byte1 << 8;
        tmpIONDataSize |= _alData.header.length.byte0;     
        _alData.IONData = new unsigned char[tmpIONDataSize];
        if( _alData.IONData != NULL )
        {
            memcpy( _alData.IONData, tmpData + alHeaderSize, tmpIONDataSize );
        }
        else
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << tmpSize
                                      << " bytes in CtiIONApplicationLayer ctor - setting payload to zero, setting valid = FALSE"  << endl;
            _alData.header.length.byte1 = 0;
            _alData.header.length.byte0 = 0;
            _valid = FALSE;
        }

        delete [] tmpData;
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << tmpSize
                                  << " bytes in CtiIONApplicationLayer ctor for scratch space - not initializing object"  << endl;
        _valid = FALSE;
    }
}


void CtiIONApplicationLayer::freeMemory( void )
{
    if( _alData.IONData != NULL )
        delete [] _alData.IONData;
    
    _alData.IONData = NULL;
}


void CtiIONApplicationLayer::putSerialized( unsigned char *buf )
{
    int destALHeaderSize, alDataStreamSize, sourceOffset, destOffset;

    alDataStreamSize  = _alData.header.length.byte1 << 8;
    alDataStreamSize |= _alData.header.length.byte0;
    destALHeaderSize = sizeof( _alData.header );
    sourceOffset = 0;
    destOffset = 0;
    
    //  copy the service bytes
    memcpy( buf, &_alData.header, 2 );
    sourceOffset += 2;
    destOffset += 2;
    
    //  if it's a request message
    if( isRequest( ) )
    {
        //  request messages don't have the status byte in the header - move past it
        sourceOffset++;
        //  no status byte makes the resulting header one byte smaller - 1 byte less to copy
        destALHeaderSize--;
    }
        
    //  copy the relevant header data
    memcpy( buf + destOffset, ((unsigned char *)&_alData.header) + sourceOffset, destALHeaderSize - destOffset );
    
    //  copy the payload data
    memcpy( buf + destALHeaderSize, _alData.IONData, alDataStreamSize );
}


unsigned int CtiIONApplicationLayer::getSerializedLength( void )
{
    int alSize;

    //  add the payload length plus the header length
    alSize  = _alData.header.length.byte1 << 8;
    alSize |= _alData.header.length.byte0;
    alSize += sizeof( _alData.header );
    
    //  if it's a request message, it doesn't have the status byte in the header
    if( isRequest( ) )
        alSize--;

    return alSize;
}


void CtiIONApplicationLayer::putPayload( unsigned char *buf )
{
    memcpy( buf, _alData.IONData, getPayloadLength( ) );
}


int CtiIONApplicationLayer::getPayloadLength( void )
{
    int alSize;

    alSize  = _alData.header.length.byte1 << 8;
    alSize |= _alData.header.length.byte0;
    
    return alSize;
}



CtiIONNetworkLayer::CtiIONNetworkLayer( )
{  
    _valid = FALSE;
    _nlData.data = NULL;  
}


void CtiIONNetworkLayer::init( CtiIONDataLinkLayer &dllLayer )
{
    unsigned char *tmpDLLData;
    int            tmpDLLDataLength, nlDataLength;

    
    freeMemory( );

    _valid = TRUE;
    
    initReserved( );
    
    tmpDLLDataLength = dllLayer.getPayloadLength( );
    tmpDLLData = new unsigned char[tmpDLLDataLength];
    
    if( tmpDLLData != NULL )
    {
        //  grab the data from the data link layer
        dllLayer.putPayload( tmpDLLData );

        //  copy the header
        memcpy( &(_nlData.header), tmpDLLData, sizeof(_nlData.header) );

        _srcID  = _nlData.header.src.byte1 << 256;
        _srcID |= _nlData.header.src.byte0;

        _dstID  = _nlData.header.dst.byte1 << 256;
        _dstID |= _nlData.header.

        nlDataLength = tmpDLLDataLength - sizeof(_nlData.header);
        _nlData.data = new unsigned char[nlDataLength];
        
        if( _nlData.data != NULL )
        {
            //  copy the data
            memcpy( _nlData.data, tmpDLLData + sizeof( _nlData.header ), tmpDLLDataLength - sizeof( _nlData.header ) );
        }
        else
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            if( tmpDLLData == NULL )
                dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << nlDataLength << " bytes in CtiIONNetworkLayer ctor;"
                                                                         << "  setting zero-length data payload, valid = FALSE" << endl;
            _nlData.header.length.byte1 = 0;
            _nlData.header.length.byte0 = 0;
            _valid = FALSE;
        }
        
        delete [] tmpDLLData;
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << tmpDLLDataLength << " bytes in CtiIONNetworkLayer ctor;"
                                                                 << "  setting valid = FALSE" << endl;
        _valid = FALSE;
    }
}


void CtiIONNetworkLayer::init( CtiIONApplicationLayer &appLayer, int msgID, int srcID, int dstID )
{
    int tmpAppDataSize, netSize;
    
    
    freeMemory( );
    
    _valid = TRUE;
    initReserved( );
    
    tmpAppDataSize = appLayer.getSerializedLength( );

    netSize = tmpAppDataSize + sizeof( _nlData.header );
    _nlData.header.length.byte1 = (netSize & 0xFF00) >> 8;
    _nlData.header.length.byte0 =  netSize & 0x00FF;

    _nlData.header.msgid.byte1 = (msgID & 0xFF00) >> 8;
    _nlData.header.msgid.byte0 =  msgID & 0x00FF;

    _nlData.header.desc.msgtype    = 1;
    _nlData.header.desc.passwdsize = 0;
    _nlData.header.desc.timesetmsg = 0;

    _srcID = srcID;
    _nlData.header.src.byte1 = (srcID & 0xFF00) >> 8;
    _nlData.header.src.byte0 = (srcID & 0x00FF);
    
    _dstID = dstID;
    _nlData.header.dst.byte1 = (dstID & 0xFF00) >> 8;
    _nlData.header.dst.byte0 = (dstID & 0x00FF);
    
    _nlData.header.service = 1;  //  only supported value

    _nlData.header.msgType = 0;  //  ION message

    _nlData.data = new unsigned char[tmpAppDataSize];

    if( _nlData.data != NULL )
    {   
        appLayer.putSerialized( _nlData.data );
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << tmpAppDataSize << " bytes in CtiIONNetworkLayer ctor;"
                                                                 << "  setting zero-length data payload, valid = FALSE" << endl;
        netSize = sizeof( _nlData.header );
        _nlData.header.length.byte1 = (netSize & 0xFF00) >> 8;
        _nlData.header.length.byte0 =  netSize & 0x00FF;
        _valid = FALSE;
    }
}


void CtiIONNetworkLayer::freeMemory( void )
{
    if( _nlData.data != NULL )
        delete [] _nlData.data;

    _nlData.data = NULL;
}


void CtiIONNetworkLayer::putSerialized( unsigned char *buf )
{
    //  copy the header
    memcpy( buf, &(_nlData.header), sizeof(_nlData.header) );
    //  then the data, offset after the header
    memcpy( buf + sizeof(_nlData.header), _nlData.data, getPayloadLength( ) );
}


unsigned int CtiIONNetworkLayer::getSerializedLength( void )
{
    int tmpLength;

    tmpLength  = _nlData.header.length.byte0;
    tmpLength |= _nlData.header.length.byte1 << 8;
    
    return tmpLength;
}


void CtiIONNetworkLayer::putPayload( unsigned char *buf )
{
    //  copy the payload data
    memcpy( buf, _nlData.data, getPayloadLength( ) );
}


int CtiIONNetworkLayer::getPayloadLength( void )
{
    return getSerializedLength( ) - sizeof(_nlData.header);
}



void CtiIONDataLinkLayer::setToOutput( CtiIONNetworkLayer &netLayer, int srcID, int dstID )
{
    freeMemory( );

    _valid = TRUE;
    
    _dataSent = 0;
    _src = srcID;
    _dst = dstID;
    _status = OutDataReady;
    _direction = Output;
    _retries = IONRetries;
    
    _dataLength = netLayer.getSerializedLength( );
    _data = new unsigned char[_dataLength];

    if( _data != NULL )
    {
        netLayer.putSerialized( _data );
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
    _data = NULL;
    _currentFrame = -1;
    _status = InDataReady;
    _direction = Input;
    _retries = IONRetries;
}


void CtiIONDataLinkLayer::freeMemory( void )
{
    int i;

    if( _frameVector.size( ) )
    {
        for( i = 0; i < _frameVector.size( ); i++ )
        {
            //  delete all new'd instances
            delete _frameVector[i];
        }
    }
    
    if( _data != NULL )
        delete [] _data;

    _data = NULL;
}


void CtiIONDataLinkLayer::putPayload( unsigned char *buf )
{
    int i, offset;

    switch( _direction )
    {
        case Input:
            {
                offset = 0;
                for( i = 0; i < _frameVector.size( ); i++ )
                {
                    _frameVector[i]->putPayload( buf + offset );
                    offset += _frameVector[i]->getPayloadLength( );
                }
            }

        case Output:
            {
                memcpy( buf, _data, getPayloadLength( ) );
            }
    }
}


int CtiIONDataLinkLayer::getPayloadLength( void )
{
    int i, payloadLength = 0;

    switch( _direction )
    {
        case Input:
            {
                for( i = 0; i < _frameVector.size( ); i++ )
                {
                    payloadLength += _frameVector[i]->getPayloadLength( );
                }
                break;
            }

        case Output:
            {
                payloadLength = _dataLength;
            }
    }

    return payloadLength;
}


int CtiIONDataLinkLayer::inFrame( unsigned char *data, unsigned long dataLength )
{
    CtiIONFrame *tmpFrame;
    
    tmpFrame = new CtiIONFrame( data, dataLength );

    if( tmpFrame != NULL )
    {
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
                            _frameVector.push_back( tmpFrame );
                            //  we're ready to send an ACK
                            _status = OutAckReady;
                        }
                        //  if it's the right frame
                        else if( _currentFrame == tmpFrame->getCounter( ) )
                        {
                            //  put the frame in the list
                            _frameVector.push_back( tmpFrame );
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


int CtiIONDataLinkLayer::outFrame( unsigned char *data, unsigned long *len )
{
    int bytesInNewFrame, numFrames, tmpCRC;

    CtiIONFrame *tmpFrame;

    //  all fields in here are set as master-oriented parameters (always setting frame to be
    //    from master to slave, etc)
    
    cout << "in outFrame" << endl;
    
    tmpFrame = new CtiIONFrame;

    if( tmpFrame != NULL )
    {
        cout << "tmpFrame != NULL " << endl;

        switch( _direction )
        {
            case Output:
            {
                cout << "in output" << endl;

                bytesInNewFrame = _dataLength - _dataSent;
                if( bytesInNewFrame > 238 )
                    bytesInNewFrame = 238;
            
                if( _dataSent == 0 )
                {
                    //  number of full frames
                    _currentFrame = _dataLength / 238;
                    //  plus a partial frame, if need be
                    if( _dataLength % 238 )
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
                
                tmpFrame->putSerialized( data );
                *len = tmpFrame->getSerializedLength( );
        
                cout << "len = " << *len << endl;

                _bytesInLastFrame = bytesInNewFrame;
                
                break;
            }

            case Input:
            {
                cout << "in input" << endl;

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
            
                tmpFrame->putSerialized( data );
                *len = tmpFrame->getSerializedLength( );
        
                break;
            }
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



CtiIONFrame::CtiIONFrame( )
{
    initReserved( );
    _frame.header.sync = 0x14;  //  start of data
    _frame.header.fmt  = 0xAC;  //  format = ION frame
    _frame.header.len  = 7;
}


CtiIONFrame::CtiIONFrame( unsigned char *rawFrame, int rawFrameLength )
{
    //  only copy what we have room for...  
    if( rawFrameLength > 252 )
        rawFrameLength = 252;

    memcpy( &_frame, rawFrame, rawFrameLength );
}


void CtiIONFrame::putSerialized( unsigned char *buf )
{
    memcpy( buf, &_frame, getSerializedLength( ) );
}


unsigned int CtiIONFrame::getSerializedLength( void )
{
    return _frame.header.len + 5;
}


void CtiIONFrame::setPayload( unsigned char *buf, int len )
{
    if( len )
    {
        if( len > 238 )
            len = 238;

        memcpy( _frame.data, buf, len );
    }

    _frame.header.len = len + 7;
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

    dataLen = _frame.header.len - 7;

    frameCRC = crc16( _frame.data - 8, dataLen + 8 );  //  CRC is computed on the data plus the 8 bytes preceding
    
    _frame.data[dataLen]   =  frameCRC & 0x00FF;        //  the bytes right after the data ends 
    _frame.data[dataLen+1] = (frameCRC & 0xFF00) << 8;  //
}


int CtiIONFrame::crcIsValid( void )
{
    unsigned int frameCRC, computedCRC;
    int dataLen;

    dataLen = _frame.header.len - 7;  //  len = data length + 7
    
    
    frameCRC  = _frame.data[dataLen];         //  the bytes right after the data ends 
    frameCRC += _frame.data[dataLen+1] << 8;  //    

    computedCRC = crc16( _frame.data - 8, dataLen + 8 );  //  CRC is computed on the data plus the 8 bytes preceding
    
    return frameCRC == computedCRC;
}


unsigned int CtiIONFrame::crc16( unsigned char *data, int length )
{
    //  CRC-16 computation
    //    from http://www.programmingparadise.com/vs/?crc/crcfast.c.html
    //    original author unknown, so i figured it was okay to use.
   
    unsigned int tmp, 
                 crc = 0;

    unsigned int crc16table[256] =
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
        tmp = crc ^ (unsigned int)data[i];
        crc = (crc >> 8) ^ crc16table[tmp & 0x00FF];
    }
    
    return crc;
}

