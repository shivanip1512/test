/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_network.cpp
 *
 * Classes: CtiIONNetworkLayer,
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

#include "ion_net_network.h"

CtiIONNetworkLayer::CtiIONNetworkLayer( )
{
    _netOut.data = NULL;
    _netIn.data  = NULL;

    _msgCount = 0;
}


CtiIONNetworkLayer::~CtiIONNetworkLayer( )
{
    freeOutPacketMemory();
    freeInPacketMemory();
}


void CtiIONNetworkLayer::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _srcID = srcID;
    _dstID = dstID;

    _datalinkLayer.setAddresses(_srcID, _dstID);
}


void CtiIONNetworkLayer::initOutPacketReserved( void )
{
    _netOut.header.length.reserved = 0;
    _netOut.header.desc.reserved   = 0;
    _netOut.header.src.reserved0_0 = 0;
    _netOut.header.src.reserved0_1 = 0;
    _netOut.header.src.reserved1   = 1;
    _netOut.header.dst.reserved0_0 = 0;
    _netOut.header.dst.reserved0_1 = 0;
    _netOut.header.dst.reserved1   = 1;
}


void CtiIONNetworkLayer::initInPacketReserved( void )
{
    _netIn.header.length.reserved = 0;
    _netIn.header.desc.reserved   = 0;
    _netIn.header.src.reserved0_0 = 0;
    _netIn.header.src.reserved0_1 = 0;
    _netIn.header.src.reserved1   = 1;
    _netIn.header.dst.reserved0_0 = 0;
    _netIn.header.dst.reserved0_1 = 0;
    _netIn.header.dst.reserved1   = 1;
}


void CtiIONNetworkLayer::setOutPayload( CtiIONSerializable &payload )
{
    int payloadSize, netSize;

    freeOutPacketMemory( );

    initOutPacketReserved( );

    _msgCount++;

    payloadSize = payload.getSerializedLength( );

    netSize = payloadSize + sizeof( _netOut.header );
    _netOut.header.length.byte1 = (netSize & 0xFF00) >> 8;
    _netOut.header.length.byte0 =  netSize & 0x00FF;

    _netOut.header.msgid.byte1 = (_msgCount & 0xFF00) >> 8;
    _netOut.header.msgid.byte0 =  _msgCount & 0x00FF;

    _netOut.header.desc.msgtype    = 1;
    _netOut.header.desc.passwdsize = 0;
    _netOut.header.desc.timesetmsg = 0;

    _netOut.header.src.byte1 = (_srcID & 0xFF00) >> 8;
    _netOut.header.src.byte0 = (_srcID & 0x00FF);

    _netOut.header.dst.byte1 = (_dstID & 0xFF00) >> 8;
    _netOut.header.dst.byte0 = (_dstID & 0x00FF);

    _netOut.header.service = 1;  //  only supported value

    _netOut.header.msgType = 0;  //  ION message

    _netOut.data = CTIDBG_new unsigned char[payloadSize];

    if( _netOut.data != NULL )
    {
        payload.putSerialized( _netOut.data );

        _datalinkLayer.setToOutput(*this);
    }
    else
    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << payloadSize << " bytes in CtiIONNetworkLayer ctor;"
                                                                 << "  setting zero-length data payload, valid = FALSE" << endl;
        netSize = sizeof( _netOut.header );
        _netOut.header.length.byte1 = (netSize & 0xFF00) >> 8;
        _netOut.header.length.byte0 =  netSize & 0x00FF;
    }
}


int CtiIONNetworkLayer::generate( CtiXfer &xfer )
{
/*    if( _netLayer.isTransactionComplete() )
    {

    }
*/
    return _datalinkLayer.generate( xfer );
}


int CtiIONNetworkLayer::decode( CtiXfer &xfer, int status )
{
    return _datalinkLayer.decode( xfer, status );
}


void CtiIONNetworkLayer::freeOutPacketMemory( void )
{
    if( _netOut.data != NULL )
        delete [] _netOut.data;

    _netOut.data = NULL;
}


void CtiIONNetworkLayer::freeInPacketMemory( void )
{
    if( _netIn.data != NULL )
        delete [] _netIn.data;

    _netIn.data = NULL;
}


void CtiIONNetworkLayer::putSerialized( unsigned char *buf ) const
{
    //  copy the header
    memcpy( buf, &(_netOut.header), sizeof(_netOut.header) );
    //  then the data, offset after the header
    memcpy( buf + sizeof(_netOut.header), _netOut.data, getPayloadLength( ) );
}


unsigned int CtiIONNetworkLayer::getSerializedLength( void ) const
{
    int tmpLength;

    tmpLength  = _netOut.header.length.byte0;
    tmpLength |= _netOut.header.length.byte1 << 8;

    return tmpLength;
}


void CtiIONNetworkLayer::putPayload( unsigned char *buf )
{
    //  copy the payload data
    memcpy( buf, _netIn.data, getPayloadLength( ) );
}


int CtiIONNetworkLayer::getPayloadLength( void ) const
{
    return getSerializedLength( ) - sizeof(_netIn.header);
}


