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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/06/20 21:00:37 $
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
                packetLen = dataLen + 1;

                //  set up the transport header
                _outPacket.header.first = first;
                _outPacket.header.final = final;
                _outPacket.header.seq   = _seq;

                //  copy the app layer chunk in - leave room for the transport header
                memcpy( (void *)_outPacket.data, (void *)&(_outAppLayer[_outAppLayerSent]), dataLen );

                _datalink.setToOutput((unsigned char *)&_outPacket, packetLen, _dstAddr, _srcAddr);

                //  datalink layer guarantees transmission, so we take it for granted
                _seq++;
                _outAppLayerSent += dataLen;

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
    int retVal;

    retVal = _datalink.decode(xfer, status);

    if( retVal )
    {
        _ioState = Failed;
    }
    else
    {
        if( _datalink.isTransactionComplete() )
        {
            switch( _ioState )
            {
                case Output:
                {
                    if( _outAppLayerLen == _outAppLayerSent )
                    {
                        _ioState = Complete;
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
    }

    return retVal;
}


bool CtiDNPTransport::isTransactionComplete( void )
{
    return _ioState == Complete;
}


int CtiDNPTransport::getInputSize( void )
{
    return _inAppLayerRecv;
}

