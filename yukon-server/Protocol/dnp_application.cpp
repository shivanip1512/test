#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_application
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
#include "dnp_application.h"

CtiDNPApplication::CtiDNPApplication()  { }

CtiDNPApplication::CtiDNPApplication(const CtiDNPApplication &aRef) { }

CtiDNPApplication::~CtiDNPApplication() { }

CtiDNPApplication &CtiDNPApplication::operator=(const CtiDNPApplication &aRef)
{
    return *this;
}


void CtiDNPApplication::reset( void )
{
    memset( &_appBuf, 0, sizeof(_appBuf) );
    _transport.reset();

    //  reset the apparent appbuf size
    _appbufBytesUsed = 0;

    _hasPoints = false;
}


void CtiDNPApplication::setCommand( AppFuncCode func )
{
    reset();
    _appBuf.req.func_code = func;
}


void CtiDNPApplication::initForOutput( void )
{
    //  these values aren't necessarially static...  just likely to be so in our infantile requests
    _appBuf.req.ctrl.first       = 1;
    _appBuf.req.ctrl.final       = 1;
    _appBuf.req.ctrl.app_confirm = 0;
    _appBuf.req.ctrl.unsolicited = 0;

    _seqno = 0;
    _appBuf.req.ctrl.seq = _seqno;

    _transport.initForOutput((unsigned char *)(&_appBuf), _appbufBytesUsed);
}


void CtiDNPApplication::initForInput( void )
{
    reset();
    _transport.initForInput();
}


void CtiDNPApplication::addPoint( dnp_point_descriptor *point )
{
    int toCopy;

    toCopy = 3;

    //  figure out the range size (anywhere from 0 to 8)
    switch( point->qual_code )
    {
        case 2:
            toCopy += 8;
            break;

        case 1:
        case 9:
            toCopy += 4;
            break;

        case 0:
        case 8:
            toCopy += 2;
            break;

        case 7:
            toCopy += 1;
            break;

        case 3:
        case 4:
        case 5:
        case 6:
        case 11:
            //  += 0
            break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unknown DNPPoint->qual_code, cannot append to DNPApplication message - ignoring (may cause mangling)" << endl;
            }

            toCopy = 0;
        }
    }

    if( toCopy > 0 )
    {
        //  copy the point over
        memcpy( &(_appBuf.req.buf[_appbufBytesUsed]), point, toCopy );
        _appbufBytesUsed += toCopy;
    }
}


int CtiDNPApplication::commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    _transport.commOut(OutMessage, outList);

    if( _transport.sendComplete() )
    {
        initForInput();
    }

    return 0;
}


int CtiDNPApplication::commIn( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    _transport.commIn(InMessage, outList);

    if( _transport.inputComplete() )
    {
        _appbufBytesUsed = _transport.bufferSize() - RspHeaderSize;
        _transport.retrieveBuffer((unsigned char *)&_appBuf);
        processInput();
    }

    return 0;
}


void CtiDNPApplication::processInput( void )
{
    if( _appBuf.rsp.func_code == ResponseResponse )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            for( int i = 0; i < _appbufBytesUsed + RspHeaderSize; i++ )
            {
                dout << " " << hex << ((unsigned char *)&_appBuf)[i];

            }
        }
    }
}


bool CtiDNPApplication::hasPoints( void )
{
    return _hasPoints;
}


void CtiDNPApplication::sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
{

}

