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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:14:03 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "dnp_application.h"

CtiDNPApplication::CtiDNPApplication()
{
    _ioState = Uninitialized;
}

CtiDNPApplication::CtiDNPApplication(const CtiDNPApplication &aRef) { }

CtiDNPApplication::~CtiDNPApplication() { }

CtiDNPApplication &CtiDNPApplication::operator=(const CtiDNPApplication &aRef)
{
    return *this;
}


/*---  Scanner-side Functions  ---*/

void CtiDNPApplication::setCommand( AppFuncCode func, unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;

    _appBufReqBytesUsed = 0;

    memset( &_appBufReq, 0, sizeof(_appBufReq) );

    //  don't set the state here - only matters on the porter side

    //  these values aren't necessarially static...  just likely to be so in our infantile requests
    //  ACH: eh
    _appBufReq.ctrl.first       = 1;
    _appBufReq.ctrl.final       = 1;
    _appBufReq.ctrl.app_confirm = 0;
    _appBufReq.ctrl.unsolicited = 0;

    _seqno = 0;
    _appBufReq.ctrl.seq = _seqno;

    _appBufReq.func_code = (unsigned char)func;
}

void CtiDNPApplication::addData( unsigned char *data, int len )
{
    //  ACH:  complain if generated size > sizeof(OUTMESS.Buffer)

    if( len > 0 )
    {
        //  copy the data in
        memcpy( &(_appBufReq.buf[_appBufReqBytesUsed]), data, len );
        _appBufReqBytesUsed += len;
    }
}


int CtiDNPApplication::getLengthReq( void )
{
    return _appBufReqBytesUsed + ReqHeaderSize;
}

void CtiDNPApplication::serializeReq( unsigned char *buf )
{
    unsigned char *src;
    int srcLen;

    src    = (unsigned char *)(&_appBufReq);
    srcLen = getLengthReq();

    //  add on the addressing bytes that sit in front of the _appBufReq struct...
    //    kind of a hack, but it has to be passed layer to layer
    src    -= 2 * (sizeof(short));
    srcLen += 2 * (sizeof(short));

    //  copy the packet (and addresses) into the buffer
    memcpy(buf, src, srcLen );
}


void CtiDNPApplication::restoreRsp( unsigned char *buf, int len )
{
    //  copy the buffer into the packet
    memcpy(&_appBufRsp, buf, len);
    //  and compute the length
    _appBufRspBytesUsed = len - RspHeaderSize;
}


/*---  Porter-side functions  --*/

void CtiDNPApplication::restoreReq( unsigned char *buf, int len )
{
    unsigned char *src;
    int srcLen;

    //  move the restore point to the address vars that sit in front of the _appBufReq struct...
    src    = (unsigned char *)(&_appBufReq);
    src    -= 2 * (sizeof(short));

    srcLen = len;

    //  copy the buffer into the packet
    memcpy(src, buf, len);

    //  and compute the length
    //    knock off the address byte length
    srcLen  -= 2 * (sizeof(short));
    _appBufReqBytesUsed = srcLen - ReqHeaderSize;

    _ioState = Output;

    //  wipe out the state of the underlying layer - its data has just changed
    _transport.initForOutput((unsigned char *)&_appBufReq, getLengthReq(), _dstAddr, _srcAddr);
}


int CtiDNPApplication::getLengthRsp( void )
{
    return _appBufRspBytesUsed + RspHeaderSize;
}


void CtiDNPApplication::serializeRsp( unsigned char *buf )
{
    //  copy the packet into the buffer
    memcpy(buf, &_appBufRsp, getLengthRsp() );
}

//  reply is always expected...  this code is deprecated, soon to be removed
/*
bool CtiDNPApplication::isReplyExpected( void )
{
    bool reply = false;

    if( _appBufReq.func_code == RequestRead ) // || app layer confirm, eventually
        reply = true;

    return reply;
}
*/

bool CtiDNPApplication::isTransactionComplete( void )
{
    bool complete;

    //  will always receive Application Layer ack/status (two bytes of indications)
    complete = _transport.sendComplete() && _transport.recvComplete();

    //  ACH:  add code to allow fragmented application layer packets to be sent and received
    //    ...will be on Scanner side...

    return complete;
}


int CtiDNPApplication::generate( CtiXfer &xfer )
{
    _transport.generate(xfer);

    return 0;
}


int CtiDNPApplication::decode( CtiXfer &xfer, int status )
{
    _transport.decode(xfer, status);

    if( _transport.sendComplete() )
    {
        _transport.initForInput((unsigned char *)&_appBufRsp);
    }

    if( _transport.recvComplete() )
    {
        _appBufRspBytesUsed = _transport.getInputSize() - RspHeaderSize;
        processInput();
    }

    return 0;
}


void CtiDNPApplication::processInput( void )
{
    if( _appBufRsp.func_code == ResponseResponse )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            for( int i = 0; i < _appBufRspBytesUsed + RspHeaderSize; i++ )
            {
                dout << " " << hex << ((unsigned char *)&_appBufRsp)[i];

            }
        }
    }
}


bool CtiDNPApplication::inHasPoints( void )
{
    return _inHasPoints;
}


void CtiDNPApplication::sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
{

}




/*
void CtiDNPApplication::initForOutput( void )
{
    unsigned char *src;
    int srcLen;

    src    = (unsigned char *)(&_appBufReq);
    srcLen = _appBufReqBytesUsed + ReqHeaderSize;

    //  ACH:  initialization error codes
    _transport.initForOutput( src, srcLen, _dstAddr, _srcAddr );
}


void CtiDNPApplication::initForInput( void )
{
    _appBufRspBytesUsed = 0;
    _inHasPoints = false;

    memset( &_appBufRsp, 0, sizeof(_appBufRsp) );
}
*/

