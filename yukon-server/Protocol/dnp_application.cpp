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
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2003/12/26 17:24:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


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
/*    if( !_outObjectBlocks.empty() )
    {
        eraseOutboundObjectBlocks();
    }*/

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


void CtiDNPApplication::setAddresses( unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;

    _transport.setAddresses(_dstAddr, _srcAddr);
}


void CtiDNPApplication::setOptions( int options )
{
    _transport.setOptions(options);
}


/*---  Scanner-side Functions  ---*/

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
    //          Actually, move all this to Porter-side;  slim down the OutMess requests like the ION.
    //          Moving all this back and forth is pointless.

    //  Also, I wonder if it would be desirable to add pointers to the outboundObjectBlocks structure,
    //    and only do the serialization when the transport layer gets initialized.
    objBlockLen = objBlock.getSerializedLen();

    if( (objBlockLen > 0) &&
        (objBlockLen < (ApplicationBufferSize - _appReqBytesUsed)) )
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

    //  copy the packet into the buffer
    memcpy(buf, src, srcLen);
}


void CtiDNPApplication::restoreRsp( unsigned char *buf, int len )
{
    if( len > RspHeaderSize )
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

    if( gDNPVerbose )
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

    //  ACH:  if class_1 || class_2 || class_3, we need to do something...  pass it up to the protocol layer, eh?

    int processed = 0;

    eraseInboundObjectBlocks();

    while( processed < _appRspBytesUsed )
    {
        CtiDNPObjectBlock *tmpDOB = CTIDBG_new CtiDNPObjectBlock();

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

/*
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
*/

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
    const CtiDNPTimeCTO *cto;

    for( int i = 0; i < _inObjectBlocks.size(); i++ )
    {
        if( _inObjectBlocks[i]->isCTO() )
        {
            cto = _inObjectBlocks[i]->getCTO();
        }
        else if( _inObjectBlocks[i]->hasPoints() )
        {
            _inObjectBlocks[i]->getPoints(pointList, cto);
        }
    }
}


//  these set of functions assume that the control output (and corresponding input) is
//    the ONLY (or first, at least) thing in the application layer.

bool CtiDNPApplication::isControlResult( void ) const
{
    bool hasControlResult = false;

    if( _inObjectBlocks.size() && _inObjectBlocks[0]->isBinaryOutputControl() )
    {
        hasControlResult = true;
    }

    return hasControlResult;
}


int CtiDNPApplication::getControlResultStatus( void ) const
{
    int retVal = -1;

    if( isControlResult() )
    {
        retVal = _inObjectBlocks[0]->getBinaryOutputControlStatus();
    }

    return retVal;
}


long CtiDNPApplication::getControlResultOffset( void ) const
{
    long result = NULL;

    if( isControlResult() )
    {
        result = _inObjectBlocks[0]->getBinaryOutputControlOffset();
    }

    return result;
}


//  These two functions need to be rewritten to get around the whole object block construct...
//    it's clunky and unnecessary, and just serves to seperate the application layer from the
//    individual

bool CtiDNPApplication::hasTimeResult( void ) const
{
    bool hasTimeResult = false;

    if( _inObjectBlocks.size() && _inObjectBlocks[0]->isTime() )
    {
        hasTimeResult = true;
    }

    return hasTimeResult;
}


unsigned long CtiDNPApplication::getTimeResult( void ) const
{
    int retVal = -1;

    if( hasTimeResult() )
    {
        retVal = _inObjectBlocks[0]->getTimeSeconds();
    }

    return retVal;
}



/*---  Porter-side functions  ---*/

void CtiDNPApplication::resetLink( void )
{
    _transport.resetLink();
}


void CtiDNPApplication::restoreReq( unsigned char *buf, int len )
{
    //  copy the buffer into the packet
    memcpy(&_appReq, buf, len);

    _appReqBytesUsed = len - ReqHeaderSize;

    _ioState    = Output;
    _retryState = _ioState;

    _comm_errors = 0;
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

    return _ioState == Complete || _ioState == Failed;
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
    if( _transport.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Input:
            {
                _transport.initForInput((unsigned char *)&_appRsp);

                break;
            }

            case Output:
            {
                _transport.initForOutput((unsigned char *)&_appReq, _appReqBytesUsed + ReqHeaderSize, _dstAddr, _srcAddr);

                break;
            }

            case OutputAck:
            {
                //  This is done here instead of

                generateAck(&_appAck, _appRsp.ctrl.seq);

                _transport.initForOutput((unsigned char *)&_appAck, sizeof(_appAck), _dstAddr, _srcAddr);

                break;
            }

            default:
            {
                break;
            }
        }
    }

    return _transport.generate(xfer);
}


int CtiDNPApplication::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;
    int transportStatus;

    transportStatus = _transport.decode(xfer, status);

    if( _transport.errorCondition() )
    {
        if( ++_comm_errors < gDNPInternalRetries )  // CommRetries )
        {
            _ioState = _retryState;
        }
        else
        {
            _ioState    = Failed;
            _retryState = _ioState;

            retVal   = transportStatus;
        }

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if( _transport.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                if( isReplyExpected() )
                {
                    _ioState    = Input;
                    //  leave _retryState on Output, so we re-output on error
                    //  _retryState = _ioState;
                }
                else
                {
                    _ioState    = Complete;
                    _retryState = _ioState;
                }

                break;
            }

            case Input:
            {
                if( _transport.getInputSize() >= RspHeaderSize )
                {
                    _appRspBytesUsed = _transport.getInputSize() - RspHeaderSize;

                    if( _appRsp.ctrl.app_confirm )
                    {
                        _ioState    = OutputAck;
                        _retryState = _ioState;
                    }
                    else
                    {
                        _ioState    = Complete;
                        _retryState = _ioState;
                    }
                }
                else
                {
                    _appRspBytesUsed = 0;
                    _ioState    = Failed;
                    _retryState = _ioState;
                }

                break;
            }

            case OutputAck:
            {
                _ioState    = Complete;
                _retryState = _ioState;

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - unknown state(" << _ioState << ") in CtiDNPApplication::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                _ioState    = Failed;
                _retryState = _ioState;
            }
        }
    }

    return retVal;
}


void CtiDNPApplication::generateAck( appAck *ack_packet, unsigned char seq )
{
    memset( ack_packet, 0, sizeof(*ack_packet) );

    ack_packet->func_code = RequestConfirm;

    ack_packet->ctrl.seq         = seq;

    ack_packet->ctrl.first       = 1;
    ack_packet->ctrl.final       = 1;

    ack_packet->ctrl.app_confirm = 0;
    ack_packet->ctrl.unsolicited = 0;
}

