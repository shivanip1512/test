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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:14:03 $
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

void CtiDNPTransport::reset( void )
{
}


int CtiDNPTransport::initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr)
{
    int retVal = NoError;

//    reset();

    _srcAddr = srcAddr;
    _dstAddr = dstAddr;

    if( len > 0 )
    {
        _outAppLayer     = buf;
        _outAppLayerLen  = len;
        _outAppLayerSent = 0;

        _seq = 0;

        _ioState = Output;

        _datalink.reset();
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

//    reset();

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

                retVal = _datalink.generate(xfer);

                //  datalink layer guarantees transmission, so we take it for granted
                _seq++;
                _outAppLayerSent += dataLen;

                break;
            }

            case Input:
            {
                //  ACH: generate ACK for previous packet (or does it do that automagically?)

                _datalink.setToInput();

                retVal = _datalink.generate(xfer);

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

    return retVal;
}


int CtiDNPTransport::decode( CtiXfer &xfer, int status )
{
    int retVal;

    retVal = _datalink.decode(xfer, status);

    if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                //  ACH: verify secondary ACK (or will datalink layer do that?)

                //  ???: do we always expect a response?
                if( _outAppLayerLen == _outAppLayerSent )
                {
                    _ioState = Input;
                }

                break;
            }

            case Input:
            {
                if( _datalink.isTransactionComplete() )
                {
                    //  copy out the data
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


bool CtiDNPTransport::sendComplete( void )
{
    bool retVal = false;

    if( _outAppLayerLen == _outAppLayerSent || _ioState != Output /* mildly redundant */ )
        retVal = true;

    return retVal;
}


bool CtiDNPTransport::recvComplete( void )
{
    bool retVal = false;

    if( _ioState == Complete )
        retVal = true;

    return retVal;
}


int CtiDNPTransport::getInputSize( void )
{
    return _inAppLayerLen;
}
