#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/07/16 13:58:00 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "prot_dnp.h"
#include "dnp_transport.h"

CtiDNPTransport::CtiDNPTransport()
{
    _ioState     = Uninitialized;
    _outAppLayer = NULL;
    _inAppLayer  = NULL;
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


int CtiDNPTransport::initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr)
{
    int retVal = NoError;

    _srcAddr = srcAddr;
    _dstAddr = dstAddr;

    if( len > 0 )
    {
        _outAppLayer     = buf;
        _outAppLayerLen  = len;
        _outAppLayerSent = 0;

        _seq = 0;

        _ioState = Output;
    }
    else
    {
        _outAppLayer    = NULL;
        _outAppLayerLen = 0;
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

    _inAppLayer     = buf;
    _inAppLayerLen  = 0;
    _inAppLayerRecv = 0;

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

                first = !(_outAppLayerSent > 0);

                dataLen = _outAppLayerLen - _outAppLayerSent;

                if( dataLen > 254 )
                {
                    dataLen = 254;
                    final = 0;
                }
                else
                {
                    final = 1;
                }

                //  add on the header byte
                packetLen = dataLen + TransportHeaderLen;

                //  set up the transport header
                _outPacket.header.first = first;
                _outPacket.header.final = final;
                _outPacket.header.seq   = _seq;

                //  copy the app layer chunk into the outbound packet
                memcpy( (void *)_outPacket.data, (void *)&(_outAppLayer[_outAppLayerSent]), dataLen );

                _datalink.setToOutput((unsigned char *)&_outPacket, packetLen, _dstAddr, _srcAddr);

                break;
            }

            case Input:
            {
                //  ACH: generate ACK for previous packet (or does it do that automagically?)

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
    }
    else if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                int transportPayloadLen;

                transportPayloadLen = _datalink.getOutPayloadLength() - TransportHeaderLen;

                _seq++;
                _outAppLayerSent += transportPayloadLen;

                if( _outAppLayerLen <= _outAppLayerSent )
                {
                    _ioState = Complete;

                    if( _outAppLayerLen < _outAppLayerSent )
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
                int inLen, dataLen;

                //  copy out the data
                inLen = _datalink.getInLength();
                _datalink.getInPayload((unsigned char *)&_inPacket);

                //  remove the header
                dataLen = inLen - sizeof(_inPacket.header);

                memcpy(&_inAppLayer[_inAppLayerRecv], _inPacket.data, dataLen);

                _inAppLayerRecv += dataLen;

                //  ACH: verify incoming sequence numbers

                if( _inPacket.header.final )
                {
                    _ioState = Complete;
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

    return retVal;
}


bool CtiDNPTransport::isTransactionComplete( void )
{
    return _ioState == Complete;
}


bool CtiDNPTransport::errorCondition( void )
{
    return _ioState == Failed;
}


int CtiDNPTransport::getInputSize( void )
{
    return _inAppLayerRecv;
}

