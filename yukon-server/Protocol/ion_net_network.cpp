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


void CtiIONNetworkLayer::setToOutput( CtiIONSerializable &payload )
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

        _ioState = Output;

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


void CtiIONNetworkLayer::setToInput( void )
{
    _ioState = Input;

    freeInPacketMemory();
    initInPacketReserved();

    _datalinkLayer.setToInput();
}


int CtiIONNetworkLayer::generate( CtiXfer &xfer )
{
    int retVal = -1;

    switch( _ioState )
    {
        case Input:
        case Output:
        {
            retVal = _datalinkLayer.generate( xfer );

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            xfer.setOutBuffer(NULL);
            xfer.setOutCount(0);
            xfer.setInBuffer(NULL);
            xfer.setInCountExpected(0);
        }
    }

    return retVal;
}


int CtiIONNetworkLayer::decode( CtiXfer &xfer, int status )
{
    int retVal = -1;

    retVal = _datalinkLayer.decode( xfer, status );

    switch( _ioState )
    {
        case Input:
        {
            if( _datalinkLayer.isTransactionComplete() )
            {
                unsigned char *tmpData;

                freeInPacketMemory();

                tmpData = new unsigned char[_datalinkLayer.getPayloadLength()];

                if( tmpData != NULL )
                {
                    int headerLen = sizeof(_netIn.header);

                    _datalinkLayer.putPayload(tmpData);

                    memcpy(&(_netIn.header), tmpData, headerLen);

                    _netIn.data = new unsigned char[_datalinkLayer.getPayloadLength() - headerLen];

                    if( _netIn.data != NULL )
                    {
                        memcpy(_netIn.data, tmpData + headerLen, _datalinkLayer.getPayloadLength() - headerLen);

                        _ioState = Complete;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        _ioState = Failed;
                    }

                    delete tmpData;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    _ioState = Failed;
                }
            }
            else if( _datalinkLayer.errorCondition() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _ioState = Failed;
            }

            break;
        }

        case Output:
        {
            if( _datalinkLayer.isTransactionComplete() )
            {
                _ioState = Complete;
            }
            else if( _datalinkLayer.errorCondition() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _ioState = Failed;
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
        }
    }

    return retVal;
}


bool CtiIONNetworkLayer::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Uninitialized;
}


bool CtiIONNetworkLayer::errorCondition( void )
{
    return _ioState == Failed;
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


//  these functions refer to the OUTBOUND packet

void CtiIONNetworkLayer::putSerialized( unsigned char *buf ) const
{
    int tmpLength;

    tmpLength  = _netOut.header.length.byte0;
    tmpLength |= _netOut.header.length.byte1 << 8;

    //  copy the header
    memcpy( buf, &(_netOut.header), sizeof(_netOut.header) );
    //  then the data, offset after the header
    memcpy( buf + sizeof(_netOut.header), _netOut.data, tmpLength );
}


unsigned int CtiIONNetworkLayer::getSerializedLength( void ) const
{
    int tmpLength;

    tmpLength  = _netOut.header.length.byte0;
    tmpLength |= _netOut.header.length.byte1 << 8;

    //  NO NO NO...  the network size bytes have already done this, see setToOutput()
    //tmpLength += sizeof(_netOut.header);

    return tmpLength;
}


//  these functions refer to the INBOUND packet

void CtiIONNetworkLayer::putPayload( unsigned char *buf )
{
    //  copy the payload data
    memcpy( buf, _netIn.data, getPayloadLength( ) );
}


int CtiIONNetworkLayer::getPayloadLength( void ) const
{
    int tmpLength;

    tmpLength  = _netIn.header.length.byte0;
    tmpLength |= _netIn.header.length.byte1 << 8;

    tmpLength -= sizeof(_netIn.header);

    return tmpLength;
}


