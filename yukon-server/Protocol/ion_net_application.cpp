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
    _appOut.IONData = NULL;
    _appIn.IONData  = NULL;

    _ioState = Uninitialized;
}


CtiIONApplicationLayer::~CtiIONApplicationLayer( )
{
    freeOutPacketMemory( );
    freeInPacketMemory( );
}


void CtiIONApplicationLayer::setAddresses( unsigned short srcID, unsigned short dstID )
{
    _networkLayer.setAddresses(srcID, dstID);
}


void CtiIONApplicationLayer::initOutPacketReserved( void )
{
    _appOut.header.servicereserved0 = 0;
    _appOut.header.servicereserved1 = 0;
    _appOut.header.length.reserved  = 0;
    _appOut.header.pid      = 0;
    _appOut.header.freq     = 1;
    _appOut.header.priority = 0;
}


void CtiIONApplicationLayer::initInPacketReserved( void )
{
    _appIn.header.servicereserved0 = 0;
    _appIn.header.servicereserved1 = 0;
    _appIn.header.length.reserved  = 0;
    _appIn.header.pid      = 0;
    _appIn.header.freq     = 1;
    _appIn.header.priority = 0;
}


void CtiIONApplicationLayer::setOutPayload( const CtiIONSerializable &payload )
{
    int itemNum, dataOffset, dataLength;

    freeOutPacketMemory( );
    initOutPacketReserved( );

    _ioState = Output;

    dataLength = payload.getSerializedLength( );

    //  fill up the header
    _appOut.header.service = 0x0F;  //  start, execute, and end the program in a single request
    //  _applicationMessage.status = -1;      //  no status byte in requests (which are all we, the master, send via the application layer)
    _appOut.header.pid  = 1;        //  can be set to anything - only one program/PID per request
    _appOut.header.freq = 1;        //  programs can currently only be executed once
    _appOut.header.priority = 0;    //  only support default priority
    _appOut.header.length.byte1 = (dataLength & 0xFF00) >> 8;
    _appOut.header.length.byte0 =  dataLength & 0x00FF;

    //  copy in the serialized datastream values
    _appOut.IONData = CTIDBG_new unsigned char[dataLength];

    if( _appOut.IONData != NULL )
    {
        payload.putSerialized(_appOut.IONData);

        _networkLayer.setOutPayload(*this);
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << dataLength << " bytes in CtiIONApplicationLayer ctor;"
                                                                 << "  proceeding with zero-length ION data payload, setting valid = FALSE" << endl;
        _appOut.header.length.byte1 = 0;
        _appOut.header.length.byte0 = 0;
        _ioState = Failed;
    }
}


int CtiIONApplicationLayer::generate( CtiXfer &xfer )
{
    return _networkLayer.generate( xfer );
}


int CtiIONApplicationLayer::decode( CtiXfer &xfer, int status )
{
    int retVal;
    int networkStatus;

    networkStatus = _networkLayer.decode(xfer, status);
#if 0
    if( _networkLayer.errorCondition() )
    {
        //  ACH:  retries...
        _ioState = Failed;
        retVal   = networkStatus;
    }
    else if( _networkLayer.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                /*if( isReplyExpected() )*/
                {
                    _networkLayer.initReply((unsigned char *)&_appIn);
                    _ioState = Input;
                }
                /*else
                {
                    _ioState = Complete;
                }*/

                break;
            }

            case Input:
            {
                if( _networkLayer.getInputSize() >= sizeof( _app_layer_struct._app_layer_header ) )
                {
                    _appRspBytesUsed = _networkLayer.getInputSize() - sizeof( _app_layer_struct._app_layer_header );
                    _ioState = Complete;
                }
                else
                {
                    _appRspBytesUsed = 0;
                    _ioState = Failed;
                    retVal = PORTREAD;  //  timeout reading from port
                }

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
#endif

    return networkStatus;
}


bool CtiIONApplicationLayer::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Uninitialized;
}


bool CtiIONApplicationLayer::errorCondition( void )
{
    return _ioState == Failed;
}


void CtiIONApplicationLayer::freeInPacketMemory( void )
{
    if( _appIn.IONData != NULL )
        delete [] _appIn.IONData;

    _appIn.IONData = NULL;
}


void CtiIONApplicationLayer::freeOutPacketMemory( void )
{
    if( _appOut.IONData != NULL )
        delete [] _appOut.IONData;

    _appOut.IONData = NULL;
}


void CtiIONApplicationLayer::putSerialized( unsigned char *buf ) const
{
    int appOutDataLen, offset;

    appOutDataLen  = _appOut.header.length.byte1 << 8;
    appOutDataLen |= _appOut.header.length.byte0;

    //  copy the header data
    memcpy( buf, (unsigned char *)&_appOut.header, sizeof(_appOut.header) );

    //  copy the payload data
    memcpy( buf + sizeof(_appOut.header), _appOut.IONData, appOutDataLen );
}


unsigned int CtiIONApplicationLayer::getSerializedLength( void ) const
{
    int appOutSize = 0;

    //  add the payload length plus the header length
    appOutSize  = _appOut.header.length.byte1 << 8;
    appOutSize |= _appOut.header.length.byte0;
    appOutSize += sizeof( _appOut.header );

    return appOutSize;
}


#if 0

//  replace with putOutbound and putInbound or something

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
#endif

