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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:25 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "prot_dnp.h"
#include "dnp_transport.h"

CtiDNPTransport::CtiDNPTransport()
{
}

CtiDNPTransport::CtiDNPTransport(const CtiDNPTransport &aRef)
{
}

CtiDNPTransport::~CtiDNPTransport()
{

}

CtiDNPTransport &CtiDNPTransport::operator=(const CtiDNPTransport &aRef)
{
    return *this;
}

void CtiDNPTransport::reset( void )
{
    if( _appLayer != NULL )
    {
        delete [] _appLayer;
        _appLayerLen = 0;
    }
}


int CtiDNPTransport::initForOutput(unsigned char *buf, int len)
{
    reset();

    if( len > 0 )
    {
        _appLayer = new unsigned char[len];

        if( _appLayer != NULL )
        {
            memcpy(buf, _appLayer, len);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Can't allocate memory for Transport layer copy of Application layer data" << endl;
            }
            len = 0;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "App layer size <= 0, not sending" << endl;
        }
    }

    return (len > 0);
}


int CtiDNPTransport::initForInput(void)
{
    reset();

    _appLayer = new unsigned char[CtiProtocolDNP::MaxAppLayerSize];

    return (_appLayer != NULL);
}


int CtiDNPTransport::commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    return 0;
}


int CtiDNPTransport::commIn( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    return 0;
}


bool CtiDNPTransport::sendComplete( void )
{
    return false;
}


bool CtiDNPTransport::inputComplete( void )
{
    return false;
}


int CtiDNPTransport::bufferSize( void )
{
    return 0;
}


void CtiDNPTransport::retrieveBuffer( unsigned char *buf )
{
}

