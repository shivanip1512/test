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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/06/24 20:00:40 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "numstr.h"
#include "dnp_application.h"

CtiDNPApplication::CtiDNPApplication()
{
    _ioState = Uninitialized;
}

CtiDNPApplication::CtiDNPApplication(const CtiDNPApplication &aRef)
{
    *this = aRef;
}

CtiDNPApplication::~CtiDNPApplication()
{
    if( !_objectList.empty() )
    {
        while( _objectList.empty() )
        {
            delete _objectList.back();

            _objectList.pop_back();
        }
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

void CtiDNPApplication::setCommand( AppFuncCode func, unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;

    _appReqBytesUsed = 0;

    memset( &_appReq, 0, sizeof(_appReq) );

    //  don't set the state here - only matters on the porter side

    //  these values aren't necessarially static...  just likely to be so in our infantile requests
    //  ACH: eh
    _appReq.ctrl.first       = 1;
    _appReq.ctrl.final       = 1;
    _appReq.ctrl.app_confirm = 0;
    _appReq.ctrl.unsolicited = 0;

    _seqno = 0;
    _appReq.ctrl.seq = _seqno;

    _appReq.func_code = (unsigned char)func;
}

void CtiDNPApplication::addData( unsigned char *data, int len )
{
    //  ACH:  complain if generated size > sizeof(OUTMESS.Buffer)

    if( len > 0 )
    {
        //  copy the data in
        memcpy( &(_appReq.buf[_appReqBytesUsed]), data, len );
        _appReqBytesUsed += len;
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
    //  copy the buffer into the packet
    memcpy(&_appRsp, buf, len);
    //  and compute the length
    _appRspBytesUsed = len - RspHeaderSize;

    _inHasPoints = false;

    //  i'm a little bit wary of this...  someday, DNPApplication needs to keep track of a larger
    //    byte buffer, and do more intelligent splitting and ack-ing.
    //  someday, this shall be moved, as we will not always have a one-to-one in/out more-to-send relationship.
    _hasOutput   = false;

    processInput();
}


void CtiDNPApplication::processInput( void )
{
    //  mere output...
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "_appRsp.ctrl.func_code = " << (unsigned)_appRsp.func_code << endl;
        dout << endl;
        dout << "_appRsp.ctrl.first       = " << (unsigned)_appRsp.ctrl.first << endl;
        dout << "_appRsp.ctrl.final       = " << (unsigned)_appRsp.ctrl.final << endl;
        dout << "_appRsp.ctrl.app_confirm = " << (unsigned)_appRsp.ctrl.app_confirm << endl;
        dout << "_appRsp.ctrl.unsolicited = " << (unsigned)_appRsp.ctrl.unsolicited << endl;
        dout << "_appRsp.ctrl.seq         = " << (unsigned)_appRsp.ctrl.seq << endl;
        dout << endl;
        dout << "_appRsp.ind.all_stations = " << _appRsp.ind.all_stations << endl;
        dout << "_appRsp.ind.already_exec = " << _appRsp.ind.already_exec << endl;
        dout << "_appRsp.ind.bad_config   = " << _appRsp.ind.bad_config << endl;
        dout << "_appRsp.ind.bad_function = " << _appRsp.ind.bad_function << endl;
        dout << "_appRsp.ind.buf_overflow = " << _appRsp.ind.buf_overflow << endl;
        dout << "_appRsp.ind.class_1      = " << _appRsp.ind.class_1 << endl;
        dout << "_appRsp.ind.class_2      = " << _appRsp.ind.class_2 << endl;
        dout << "_appRsp.ind.class_3      = " << _appRsp.ind.class_3 << endl;
        dout << "_appRsp.ind.dev_trouble  = " << _appRsp.ind.dev_trouble << endl;
        dout << "_appRsp.ind.need_time    = " << _appRsp.ind.need_time << endl;
        dout << "_appRsp.ind.obj_unknown  = " << _appRsp.ind.obj_unknown << endl;
        dout << "_appRsp.ind.out_of_range = " << _appRsp.ind.out_of_range << endl;
        dout << "_appRsp.ind.reserved     = " << _appRsp.ind.reserved << endl;
        dout << "_appRsp.ind.restart      = " << _appRsp.ind.restart << endl;
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
        setCommand(RequestConfirm, _dstAddr, _srcAddr);
        _appReq.ctrl.seq = _appRsp.ctrl.seq;
        _hasOutput = true;
    }

    int processed = 0;
    _inHasPoints = false;

    while( processed < _appRspBytesUsed )
    {
        CtiDNPObjectBlock *tmpDOB = new CtiDNPObjectBlock();

        processed += tmpDOB->restore(&(_appRsp.buf[processed]), _appRspBytesUsed - processed);

        _inHasPoints |= tmpDOB->hasPoints();
    }
}


bool CtiDNPApplication::inHasPoints( void )
{
    return _inHasPoints;
}


void CtiDNPApplication::sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
{

}


bool CtiDNPApplication::hasOutput( void )
{
    return _hasOutput;
}


/*---  Porter-side functions  --*/

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
                switch( _appReq.func_code )
                {
                    case RequestConfirm:
                    case RequestDirectOpNoAck:
                    case RequestFreezeClrNoAck:
                    case RequestFreezeWTimeNoAck:
                    case RequestImmedFreezeNoAck:
                    {
                        _ioState = Complete;
                        break;
                    }

                    default:
                    {
                        _transport.initForInput((unsigned char *)&_appRsp);
                        _ioState = Input;
                        break;
                    }
                }

                break;
            }

            case Input:
            {
                _appRspBytesUsed = _transport.getInputSize() - RspHeaderSize;

                _ioState = Complete;

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

