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
#include "precompiled.h"

#include "guard.h"
#include "logger.h"

#include "ion_net_network.h"

using std::endl;

CtiIONNetworkLayer::CtiIONNetworkLayer( ) :
    _ioState(Uninitialized),
    _valid(0),
    _masterAddress(0),
    _slaveAddress(0),
    _msgCount(0)
{
    memset( &_netOut, 0, sizeof(_net_layer_struct) );
    memset( &_netIn,  0, sizeof(_net_layer_struct) );
}


CtiIONNetworkLayer::~CtiIONNetworkLayer( )
{
    freeOutPacketMemory();
    freeInPacketMemory();
}


void CtiIONNetworkLayer::setAddresses( unsigned short masterAddress, unsigned short slaveAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _datalinkLayer.setAddresses(_masterAddress, _slaveAddress);
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


void CtiIONNetworkLayer::setToOutput( CtiIONSerializable &payload, bool timeSync )
{
    int payloadSize, netSize;

    freeOutPacketMemory();

    initOutPacketReserved();

    _msgCount++;

    payloadSize = payload.getSerializedLength();

    netSize = payloadSize + sizeof( _netOut.header );
    _netOut.header.length.byte1 = (netSize & 0xFF00) >> 8;
    _netOut.header.length.byte0 =  netSize & 0x00FF;

    _netOut.header.msgid.byte1 = (_msgCount & 0xFF00) >> 8;
    _netOut.header.msgid.byte0 =  _msgCount & 0x00FF;

    _netOut.header.desc.msgtype    = 1;
    _netOut.header.desc.passwdsize = 0;

    if( timeSync )
    {
        _netOut.header.desc.timesetmsg = 1;
    }
    else
    {
        _netOut.header.desc.timesetmsg = 0;
    }

    _netOut.header.src.byte1 = (_masterAddress & 0xFF00) >> 8;
    _netOut.header.src.byte0 = (_masterAddress & 0x00FF);

    _netOut.header.dst.byte1 = (_slaveAddress  & 0xFF00) >> 8;
    _netOut.header.dst.byte0 = (_slaveAddress  & 0x00FF);

    _netOut.header.service = 1;  //  only supported value

    if( timeSync )
    {
        _netOut.header.msgType = MessageType_TimeSync;
    }
    else
    {
        _netOut.header.msgType = MessageType_IONMessage;
    }

    _netOut.data = CTIDBG_new unsigned char[payloadSize];

    if( _netOut.data != NULL )
    {
        payload.putSerialized(_netOut.data);

        _ioState = Output;

        _datalinkLayer.setToOutput(*this);
    }
    else
    {
        CTILOG_ERROR(dout, "unable to allocate "<< payloadSize <<" bytes in CtiIONApplicationLayer ctor;  "
                "proceeding with zero-length ION data payload, setting valid = false");

        netSize = sizeof( _netOut.header );

        _ioState = Failed;

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


YukonError_t CtiIONNetworkLayer::generate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

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
            CTILOG_ERROR(dout, "unknown state "<< _ioState);

            _ioState = Failed;
            retVal = ClientErrors::BadRange;

            xfer.setOutBuffer(NULL);
            xfer.setOutCount(0);
            xfer.setInBuffer(NULL);
            xfer.setInCountExpected(0);
        }
    }

    return retVal;
}


YukonError_t CtiIONNetworkLayer::decode( CtiXfer &xfer, YukonError_t status )
{
    const YukonError_t retVal = _datalinkLayer.decode( xfer, status );

    if( _datalinkLayer.errorCondition() )
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout, "_datalinkLayer.errorCondition()");
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
                if( _datalinkLayer.isTransactionComplete() )
                {
                    int tmpSrc, tmpDst, tmpMsgID;

                    freeInPacketMemory();

                    unsigned char *tmpData;

                    tmpData = CTIDBG_new unsigned char[_datalinkLayer.getPayloadLength()];

                    int headerLen = sizeof(_netIn.header);

                    _datalinkLayer.putPayload(tmpData);

                    memcpy(&(_netIn.header), tmpData, headerLen);

                    tmpSrc   = _netIn.header.src.byte0   | (_netIn.header.src.byte1 << 8);
                    tmpDst   = _netIn.header.dst.byte0   | (_netIn.header.dst.byte1 << 8);
                    tmpMsgID = _netIn.header.msgid.byte0 | (_netIn.header.msgid.byte1 << 8);

                    if( tmpSrc == _slaveAddress && tmpDst == _masterAddress )
                    {
                        if( tmpMsgID == _msgCount )
                        {
                            _netIn.data = CTIDBG_new unsigned char[_datalinkLayer.getPayloadLength() - headerLen];

                            memcpy(_netIn.data, tmpData + headerLen, _datalinkLayer.getPayloadLength() - headerLen);

                            _ioState = Complete;
                        }
                        else if( tmpMsgID < _msgCount )
                        {
                            CTILOG_ERROR(dout, "tmpMsgID("<< tmpMsgID <<") < _msgCount("<< _msgCount <<")");

                            //  try to read/catch up until we get the one we expected
                            _ioState = Input;

                            _datalinkLayer.setToInput();
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "tmpMsgID("<< tmpMsgID <<") < _msgCount("<< _msgCount <<")");

                            _ioState = Failed;
                        }
                    }
                    else
                    {
                        if( isDebugLudicrous() )
                        {
                            CTILOG_DEBUG(dout, "network layer packet contains incorrect address");
                        }

                        _ioState = Failed;
                    }

                    delete [] tmpData;
                }

                break;
            }

            case Output:
            {
                if( _datalinkLayer.isTransactionComplete() )
                {
                    _ioState = Complete;
                }

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unknown state "<< _ioState);

                _ioState = Failed;
            }
        }
    }

    return retVal;
}


bool CtiIONNetworkLayer::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Uninitialized || _ioState == Failed;
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
    int tmpLength, headerLen;

    headerLen  = sizeof(_netOut.header);

    tmpLength  = _netOut.header.length.byte0;
    tmpLength |= _netOut.header.length.byte1 << 8;

    //  copy the header
    memcpy(buf, &(_netOut.header), headerLen);
    //  then the data, offset after the header
    memcpy(buf + headerLen, _netOut.data, tmpLength - headerLen);
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
    memcpy(buf, _netIn.data, getPayloadLength());
}


int CtiIONNetworkLayer::getPayloadLength( void ) const
{
    int tmpLength;

    tmpLength  = _netIn.header.length.byte0;
    tmpLength |= _netIn.header.length.byte1 << 8;

    tmpLength -= sizeof(_netIn.header);

    return tmpLength;
}


