/*-----------------------------------------------------------------------------*
*
* File:   dnp_transport
*
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2005/02/10 23:23:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"

#include "dnp_transport.h"

CtiDNPTransport::CtiDNPTransport()
{
    _ioState     = Uninitialized;
    _outPayload = NULL;
    _inPayload  = NULL;
}

CtiDNPTransport::CtiDNPTransport(const CtiDNPTransport &aRef)
{
    *this = aRef;
}

CtiDNPTransport::~CtiDNPTransport()
{
}

CtiDNPTransport &CtiDNPTransport::operator=(const CtiDNPTransport &aRef)
{
    if( this != &aRef )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *this;
}


void CtiDNPTransport::setAddresses(unsigned short dst, unsigned short src)
{
    _datalink.setAddresses(dst, src);
}


void CtiDNPTransport::setOptions(int options)
{
    _datalink.setOptions(options);
}


void CtiDNPTransport::resetLink( void )
{
    _datalink.resetLink();
}


int CtiDNPTransport::initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr)
{
    int retVal = NoError;

    _srcAddr = srcAddr;
    _dstAddr = dstAddr;

    if( len > 0 )
    {
        _outPayload     = buf;
        _outPayloadLen  = len;
        _outPayloadSent = 0;

        _seq = 0;

        _ioState = Output;
    }
    else
    {
        _outPayload     = NULL;
        _outPayloadLen  = 0;
        _outPayloadSent = 0;
        _ioState = Uninitialized;

        //  maybe set error return... ?

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "App layer size <= 0, not sending" << endl;
        }
    }

    return retVal;
}


int CtiDNPTransport::initForInput(unsigned char *buf)
{
    int retVal = NoError;

    _inPayload     = buf;
    _inPayloadLen  = 0;
    _inPayloadRecv = 0;

    _ioState = Input;

    return retVal;
}


int CtiDNPTransport::generate( CtiXfer &xfer )
{
    int retVal = NoError;
    int dataLen, packetLen, first, final;

    if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                //  prepare transport layer buf dude here man like and stuff for y'all

                first = !(_outPayloadSent > 0);

                _currentPacketLen = _outPayloadLen - _outPayloadSent;

                if( _currentPacketLen > TransportMaxPayloadLen )
                {
                    _currentPacketLen = TransportMaxPayloadLen;

                    final = 0;
                }
                else
                {
                    final = 1;
                }

                //  add on the header byte
                packetLen = _currentPacketLen + TransportHeaderLen;

                //  set up the transport header
                _outPacket.header.first = first;
                _outPacket.header.final = final;
                _outPacket.header.seq   = _seq;

                //  copy the app layer chunk into the outbound packet
                memcpy( (void *)_outPacket.data, (void *)&(_outPayload[_outPayloadSent]), _currentPacketLen );

                _datalink.setToOutput((unsigned char *)&_outPacket, packetLen);

                break;
            }

            case Input:
            {
                _datalink.setToInput();

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    retVal = _datalink.generate(xfer);

    return retVal;
}


int CtiDNPTransport::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;
    int datalinkStatus;

    datalinkStatus = _datalink.decode(xfer, status);

    if( _datalink.errorCondition() )
    {
        //  ACH: if( tries > maxtries ) or something
        _ioState = Failed;
        retVal   = datalinkStatus;

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                int transportPayloadLen;

                _seq++;
                _outPayloadSent += _currentPacketLen;

                if( _outPayloadLen <= _outPayloadSent )
                {
                    _ioState = Complete;

                    if( _outPayloadLen < _outPayloadSent )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }

                break;
            }

            case Input:
            {
                int dataLen;

                //  copy out the data
                if( _datalink.getInPayloadLength() >= TransportHeaderLen )
                {
                    dataLen = _datalink.getInPayloadLength() - TransportHeaderLen;
                    _datalink.getInPayload((unsigned char *)&_inPacket);

                    memcpy(&_inPayload[_inPayloadRecv], _inPacket.data, dataLen);

                    _inPayloadRecv += dataLen;

                    //  ACH: verify incoming sequence numbers

                    if( _inPacket.header.final )
                    {
                        _ioState = Complete;
                    }
                }
                else
                {
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
    }

    return retVal;
}


bool CtiDNPTransport::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Failed || _ioState == Uninitialized;
}


bool CtiDNPTransport::errorCondition( void )
{
    return _ioState == Failed;
}


int CtiDNPTransport::getInputSize( void )
{
    return _inPayloadRecv;
}

