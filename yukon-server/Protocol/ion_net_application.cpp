/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_application.cpp
 *
 * Classes: CtiIONDataStream, CtiIONApplicationLayer, CtiIONNetworkLayer,
 *            CtiIONDatalinkLayer
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

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


void CtiIONApplicationLayer::setAddresses( unsigned short masterAddress, unsigned short slaveAddress )
{
    _networkLayer.setAddresses(masterAddress, slaveAddress);
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


void CtiIONApplicationLayer::setToTimeSync( void )
{
    CtiIONTimeSync ts(RWTime::now().seconds() - rwEpoch);

    freeOutPacketMemory();
    initOutPacketReserved();

    _ioState = Output;

    _networkLayer.setToOutput(ts, true);
}


void CtiIONApplicationLayer::setToOutput( const CtiIONSerializable &payload )
{
    int itemNum, dataOffset, dataLength;

    freeOutPacketMemory();
    initOutPacketReserved();

    _ioState = Output;

    dataLength = payload.getSerializedLength();

    //  fill up the header
    _appOut.header.service = 0x0F;  //  start, execute, and end the program in a single request
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

        _networkLayer.setToOutput(*this);
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime( ) << " (" << __FILE__ << ":" << __LINE__ << ") unable to allocate " << dataLength << " bytes in CtiIONApplicationLayer ctor;"
                                                                 << "  proceeding with zero-length ION data payload, setting valid = false" << endl;
        _appOut.header.length.byte1 = 0;
        _appOut.header.length.byte0 = 0;
        _ioState = Failed;
    }
}


void CtiIONApplicationLayer::setToInput( void )
{
    _ioState = Input;

    _networkLayer.setToInput();
}


int CtiIONApplicationLayer::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    switch( _ioState )
    {
        case Input:
        case Output:
        {
            retVal = _networkLayer.generate(xfer);

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint -- unknown state " << _ioState << " in CtiIONApplicationLayer::generate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _ioState = Failed;
            retVal = BADRANGE;

            xfer.setOutBuffer(NULL);
            xfer.setOutCount(0);
            xfer.setInBuffer(NULL);
            xfer.setInCountExpected(0);
        }
    }

    return retVal;
}


int CtiIONApplicationLayer::decode( CtiXfer &xfer, int status )
{
    int retVal;

    retVal = _networkLayer.decode(xfer, status);

    if( _networkLayer.errorCondition() )
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint -- _networkLayer.errorCondition() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  error conditions propagate back up to the protocol object
        _ioState = Failed;
    }
    else
    {
        switch( _ioState )
        {
            case Input:
            {
                if( _networkLayer.isTransactionComplete() )
                {
                    unsigned char *tmpData;

                    freeInPacketMemory();

                    _ioState = Complete;

                    tmpData = CTIDBG_new unsigned char[_networkLayer.getPayloadLength()];

                    int headerLen = sizeof(_appIn.header);

                    _networkLayer.putPayload(tmpData);

                    if( _networkLayer.getPayloadLength() >= headerLen )
                    {
                        memcpy(&(_appIn.header), tmpData, headerLen);

                        _appIn.IONData = CTIDBG_new unsigned char[_networkLayer.getPayloadLength() - headerLen];

                        memcpy(_appIn.IONData, tmpData + headerLen, _networkLayer.getPayloadLength() - headerLen);
                    }
                    else
                    {
                        memcpy(&(_appIn.header), tmpData, _networkLayer.getPayloadLength());

                        _appIn.header.length.byte0 = 0;
                        _appIn.header.length.byte1 = 0;
                    }

                    delete [] tmpData;
                }

                break;
            }

            case Output:
            {
                if( _networkLayer.isTransactionComplete() )
                {
                    _ioState = Complete;
                }

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint -- unknown state " << _ioState << " in CtiIONApplicationLayer::decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _ioState = Failed;
            }
        }
    }

    return retVal;
}


bool CtiIONApplicationLayer::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Uninitialized || _ioState == Failed;
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

    appOutSize  = _appOut.header.length.byte1 << 8;
    appOutSize |= _appOut.header.length.byte0;

    appOutSize += sizeof( _appOut.header );

    return appOutSize;
}


void CtiIONApplicationLayer::putPayload( unsigned char *buf ) const
{
    memcpy( buf, _appIn.IONData, getPayloadLength( ) );
}


unsigned int CtiIONApplicationLayer::getPayloadLength( void ) const
{
    unsigned int appInSize;

    appInSize  = _appIn.header.length.byte1 << 8;
    appInSize |= _appIn.header.length.byte0;

    return appInSize;
}

