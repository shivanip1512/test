/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_application.cpp
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

#include "ctitypes.h"
#include "guard.h"
#include "logger.h"

#include "ion_net_application.h"


CtiIONApplicationLayer::CtiIONApplicationLayer( )
{
    _valid = false;
    _alData.IONData = NULL;
}


CtiIONApplicationLayer::~CtiIONApplicationLayer( )
{
    freeMemory( );
}


bool CtiIONApplicationLayer::isValid( void )
{
    return _valid;
}


bool CtiIONApplicationLayer::isRequest( void )
{
    return _alData.header.service & 0x01;
}


void CtiIONApplicationLayer::initReserved( void )
{
    _alData.header.servicereserved0 = 0;
    _alData.header.servicereserved1 = 0;
    _alData.header.length.reserved = 0;
    _alData.header.pid = 0;
    _alData.header.freq = 1;
    _alData.header.priority = 0;
}


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


/*    freeMemory( );

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
*/
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


