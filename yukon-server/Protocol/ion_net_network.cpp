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
    _valid = FALSE;
    _nlData.data = NULL;
}


void CtiIONNetworkLayer::init( CtiIONDataLinkLayer &dllLayer )
{
    unsigned char *tmpDLLData;
    int            tmpDLLDataLength, nlDataLength;

/*
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
        _dstID |= _nlData.header.dst.byte0;

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
*/    {
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << tmpDLLDataLength << " bytes in CtiIONNetworkLayer ctor;"
                                                                 << "  setting valid = FALSE" << endl;
        _valid = FALSE;
    }
}


void CtiIONNetworkLayer::init( CtiIONApplicationLayer &appLayer, int msgID, int srcID, int dstID )
{
    int tmpAppDataSize, netSize;

/*
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
*/    {
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


