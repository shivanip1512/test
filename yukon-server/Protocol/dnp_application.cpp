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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2002/10/18 20:21:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "numstr.h"
#include "dnp_application.h"

CtiDNPApplication::CtiDNPApplication()
{
    _ioState = Uninitialized;
    _seqno = 0;
}

CtiDNPApplication::CtiDNPApplication(const CtiDNPApplication &aRef)
{
    *this = aRef;
}

CtiDNPApplication::~CtiDNPApplication()
{
    if( !_outObjectBlocks.empty() )
    {
        eraseOutboundObjectBlocks();
    }

    if( !_inObjectBlocks.empty() )
    {
        eraseInboundObjectBlocks();
    }
}

CtiDNPApplication &CtiDNPApplication::operator=(const CtiDNPApplication &aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}


/*---  Scanner-side Functions  ---*/

void CtiDNPApplication::setAddresses( unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;
}


void CtiDNPApplication::setCommand( AppFuncCode func )
{
    _appReqBytesUsed = 0;

    memset( &_appReq, 0, sizeof(_appReq) );

    //  don't set the state here - only matters on the porter side

    //  these values aren't necessarially static...  just likely to be so in our infantile requests
    //  ACH: eh
    _appReq.ctrl.first       = 1;
    _appReq.ctrl.final       = 1;
    _appReq.ctrl.app_confirm = 0;
    _appReq.ctrl.unsolicited = 0;

    _appReq.ctrl.seq = ++_seqno;

    _appReq.func_code = (unsigned char)func;
}


void CtiDNPApplication::addObjectBlock( const CtiDNPObjectBlock &objBlock )
{
    int objBlockLen;
    unsigned char *tmpbuf;

    //  ACH:  complain if generated+existing size > sizeof(OUTMESS.Buffer)
    objBlockLen = objBlock.getSerializedLen();

    if( (objBlockLen > 0) &&
        (objBlockLen < (DNP_APP_BUF_SIZE - _appReqBytesUsed)) )
    {
        objBlock.serialize(_appReq.buf + _appReqBytesUsed);

        _appReqBytesUsed += objBlockLen;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


int CtiDNPApplication::getLengthReq( void )
{
    return _appReqBytesUsed + ReqHeaderSize;
}


void CtiDNPApplication::serializeReq( unsigned char *buf )
{
    unsigned char *src;
    int srcLen;

    src    = (unsigned char *)(&_appReq);
    srcLen = getLengthReq();

    //  add on the addressing bytes that sit in front of the _appReq struct...
    //    kind of a hack, but it has to be passed layer to layer
    src    -= 2 * (sizeof(short));
    srcLen += 2 * (sizeof(short));

    //  copy the packet (and addresses) into the buffer
    memcpy(buf, src, srcLen );

    //  maybe move someday... ?  see comment below in restoreInbound
    _hasOutput = false;
}


void CtiDNPApplication::restoreRsp( unsigned char *buf, int len )
{
    if( len > 0 )
    {
        //  copy the buffer into the packet
        memcpy(&_appRsp, buf, len);
        //  and compute the length
        _appRspBytesUsed = len - RspHeaderSize;

        processInput();
    }
    else
    {
        _appRspBytesUsed = 0;
        eraseInboundObjectBlocks();
    }
}


void CtiDNPApplication::processInput( void )
{
    _inHasPoints = false;

    //  mere output...
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "_appRsp.ctrl.func_code = " << (unsigned)_appRsp.func_code << endl;
        dout << endl;
        dout << "_appRsp.ctrl.first       = " << (unsigned)_appRsp.ctrl.first       << endl;
        dout << "_appRsp.ctrl.final       = " << (unsigned)_appRsp.ctrl.final       << endl;
        dout << "_appRsp.ctrl.app_confirm = " << (unsigned)_appRsp.ctrl.app_confirm << endl;
        dout << "_appRsp.ctrl.unsolicited = " << (unsigned)_appRsp.ctrl.unsolicited << endl;
        dout << "_appRsp.ctrl.seq         = " << (unsigned)_appRsp.ctrl.seq         << endl;
        dout << endl;
        dout << "_appRsp.ind.all_stations = " << _appRsp.ind.all_stations << endl;
        dout << "_appRsp.ind.already_exec = " << _appRsp.ind.already_exec << endl;
        dout << "_appRsp.ind.bad_config   = " << _appRsp.ind.bad_config   << endl;
        dout << "_appRsp.ind.bad_function = " << _appRsp.ind.bad_function << endl;
        dout << "_appRsp.ind.buf_overflow = " << _appRsp.ind.buf_overflow << endl;
        dout << "_appRsp.ind.class_1      = " << _appRsp.ind.class_1      << endl;
        dout << "_appRsp.ind.class_2      = " << _appRsp.ind.class_2      << endl;
        dout << "_appRsp.ind.class_3      = " << _appRsp.ind.class_3      << endl;
        dout << "_appRsp.ind.dev_trouble  = " << _appRsp.ind.dev_trouble  << endl;
        dout << "_appRsp.ind.need_time    = " << _appRsp.ind.need_time    << endl;
        dout << "_appRsp.ind.obj_unknown  = " << _appRsp.ind.obj_unknown  << endl;
        dout << "_appRsp.ind.out_of_range = " << _appRsp.ind.out_of_range << endl;
        dout << "_appRsp.ind.reserved     = " << _appRsp.ind.reserved     << endl;
        dout << "_appRsp.ind.restart      = " << _appRsp.ind.restart      << endl;
        dout << endl;
        dout << "data: " << endl;

        for( int i = 0; i < _appRspBytesUsed; i++ )
        {
            dout << RWCString(CtiNumStr(_appRsp.buf[i]).hex().zpad(2)) << " ";

            if( !(i % 20) && i )
                dout << endl;
        }

        dout << endl << endl;
    }

    //  real code
    if( _appRsp.ctrl.app_confirm )
    {
        setCommand(RequestConfirm);
        _appReq.ctrl.seq = _appRsp.ctrl.seq;
        _hasOutput = true;
    }

    //  if class_1 || class_2 || class_3, we need to do something...  pass it up to the protocol layer, eh?

    int processed = 0;

    eraseInboundObjectBlocks();

    while( processed < _appRspBytesUsed )
    {
        CtiDNPObjectBlock *tmpDOB = new CtiDNPObjectBlock();

        processed += tmpDOB->restore(&(_appRsp.buf[processed]), _appRspBytesUsed - processed);

        _inObjectBlocks.push_back(tmpDOB);
    }
}


void CtiDNPApplication::eraseInboundObjectBlocks( void )
{
    while( !_inObjectBlocks.empty() )
    {
        if( _inObjectBlocks.back() != NULL )
        {
            delete _inObjectBlocks.back();
        }

        _inObjectBlocks.pop_back();
    }
}


void CtiDNPApplication::eraseOutboundObjectBlocks( void )
{
    while( !_outObjectBlocks.empty() )
    {
        if( _outObjectBlocks.back() != NULL )
        {
            delete _outObjectBlocks.back();
        }

        _outObjectBlocks.pop_back();
    }
}


bool CtiDNPApplication::hasInboundPoints( void )
{
    bool hasPoints = false;

    for( int i = 0; i < _inObjectBlocks.size(); i++ )
    {
        hasPoints |= (_inObjectBlocks[i])->hasPoints();
    }

    return hasPoints;
}


void CtiDNPApplication::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    for( int i = 0; i < _inObjectBlocks.size(); i++ )
    {
        if( _inObjectBlocks[i]->hasPoints() )
        {
            _inObjectBlocks[i]->getPoints(pointList);
        }
    }
}


bool CtiDNPApplication::hasOutput( void )
{
    return _hasOutput;
}


/*---  Porter-side functions  ---*/

void CtiDNPApplication::restoreReq( unsigned char *buf, int len )
{
    unsigned char *src;
    int srcLen;

    //  move the restore point to the address vars that sit in front of the _appReq struct...
    src    = (unsigned char *)(&_appReq);
    src    -= 2 * (sizeof(short));

    srcLen = len;

    //  copy the buffer into the packet
    memcpy(src, buf, len);

    //  and compute the length
    //    knock off the address byte length
    srcLen  -= 2 * (sizeof(short));
    _appReqBytesUsed = srcLen - ReqHeaderSize;

    _ioState = Output;

    //  wipe out the state of the underlying layer - its data has just changed
    _transport.initForOutput((unsigned char *)&_appReq, getLengthReq(), _dstAddr, _srcAddr);
}


int CtiDNPApplication::getLengthRsp( void )
{
    return _appRspBytesUsed + RspHeaderSize;
}


void CtiDNPApplication::serializeRsp( unsigned char *buf )
{
    //  copy the packet into the buffer
    memcpy(buf, &_appRsp, getLengthRsp() );
}


bool CtiDNPApplication::isTransactionComplete( void )
{
    bool complete;

    //  ACH:  add code to allow fragmented application layer packets to be sent and received
    //    ...but will be on Scanner side...

    return _ioState == Complete;
}


bool CtiDNPApplication::isReplyExpected( void )
{
    bool retVal;

    switch( _appReq.func_code )
    {
        case RequestConfirm:
        case RequestDirectOpNoAck:
        case RequestFreezeClrNoAck:
        case RequestFreezeWTimeNoAck:
        case RequestImmedFreezeNoAck:
        {
            retVal = false;
            break;
        }

        default:
        {
            retVal = true;
            break;
        }
    }

    return retVal;
}


bool CtiDNPApplication::errorCondition( void )
{
    return _ioState == Failed;
}


int CtiDNPApplication::generate( CtiXfer &xfer )
{
    return _transport.generate(xfer);
}


int CtiDNPApplication::decode( CtiXfer &xfer, int status )
{
    int retVal;
    int transportStatus;

    transportStatus = _transport.decode(xfer, status);

    if( _transport.errorCondition() )
    {
        //  ACH:  retries...
        _ioState = Failed;
        retVal   = transportStatus;
    }
    else if( _transport.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                if( isReplyExpected() )
                {
                    _transport.initForInput((unsigned char *)&_appRsp);
                    _ioState = Input;
                }
                else
                {
                    _ioState = Complete;
                }

                break;
            }

            case Input:
            {
                if( _transport.getInputSize() >= RspHeaderSize )
                {
                    _appRspBytesUsed = _transport.getInputSize() - RspHeaderSize;
                    _ioState = Complete;
                }
                else
                {
                    _appRspBytesUsed = 0;
                    _ioState = Failed;
                    retVal = PORTREAD;  //  timeout reading from port
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

