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
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2005/03/17 05:25:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dllbase.h"
#include "logger.h"
#include "numstr.h"
#include "dnp_application.h"


namespace Cti       {
namespace Protocol  {
namespace DNP       {

Application::Application() :
    _ioState(Uninitialized),
    _seqno(0)
{
}

Application::Application(const Application &aRef)
{
    *this = aRef;
}

Application::~Application()
{
    eraseOutboundObjectBlocks();
    eraseInboundObjectBlocks();
}

Application &Application::operator=(const Application &aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}


void Application::setAddresses( unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;

    _transport.setAddresses(_dstAddr, _srcAddr);
}


void Application::setOptions( int options )
{
    _transport.setOptions(options);
}


void Application::setCommand( FunctionCode fc )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _final_frame_received = false;

    _request_function = fc;

    _ioState = Output;
}


void Application::addObjectBlock( const ObjectBlock *objBlock )
{
    _out_object_blocks.push(objBlock);
}


void Application::processInput( void )
{
    if( gDNPVerbose )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "_response.ctrl.func_code = " << (unsigned)_response.func_code << endl;
        dout << endl;
        dout << "_response.ctrl.first       = " << (unsigned)_response.ctrl.first       << endl;
        dout << "_response.ctrl.final       = " << (unsigned)_response.ctrl.final       << endl;
        dout << "_response.ctrl.app_confirm = " << (unsigned)_response.ctrl.app_confirm << endl;
        dout << "_response.ctrl.unsolicited = " << (unsigned)_response.ctrl.unsolicited << endl;
        dout << "_response.ctrl.seq         = " << (unsigned)_response.ctrl.seq         << endl;
        dout << endl;
        dout << "_response.ind.all_stations = " << _response.ind.all_stations << endl;
        dout << "_response.ind.already_exec = " << _response.ind.already_exec << endl;
        dout << "_response.ind.bad_config   = " << _response.ind.bad_config   << endl;
        dout << "_response.ind.bad_function = " << _response.ind.bad_function << endl;
        dout << "_response.ind.buf_overflow = " << _response.ind.buf_overflow << endl;
        dout << "_response.ind.class_1      = " << _response.ind.class_1      << endl;
        dout << "_response.ind.class_2      = " << _response.ind.class_2      << endl;
        dout << "_response.ind.class_3      = " << _response.ind.class_3      << endl;
        dout << "_response.ind.dev_trouble  = " << _response.ind.dev_trouble  << endl;
        dout << "_response.ind.need_time    = " << _response.ind.need_time    << endl;
        dout << "_response.ind.obj_unknown  = " << _response.ind.obj_unknown  << endl;
        dout << "_response.ind.out_of_range = " << _response.ind.out_of_range << endl;
        dout << "_response.ind.reserved     = " << _response.ind.reserved     << endl;
        dout << "_response.ind.restart      = " << _response.ind.restart      << endl;
        dout << endl;
        dout << "data: " << endl;

        for( int i = 0; i < _response_buf_len; i++ )
        {
            dout << RWCString(CtiNumStr(_response.buf[i]).hex().zpad(2)) << " ";

            if( !(i % 20) && i )
                dout << endl;
        }

        dout << endl << endl;
    }

    //  ACH:  if class_1 || class_2 || class_3, we need to do something...  pass it up to the protocol layer, eh?
    //  also, if need_time, do some time syncing, boieeeee

    int processed = 0;

    if( _response.ctrl.first )
    {
        eraseInboundObjectBlocks();
    }

    while( processed < _response_buf_len )
    {
        ObjectBlock *tmpOB = CTIDBG_new ObjectBlock;

        processed += tmpOB->restore(&(_response.buf[processed]), _response_buf_len - processed);

        _in_object_blocks.push(tmpOB);
    }
}


void Application::initForOutput( void )
{
    int pos = 0;

    memset( &_request, 0, sizeof(_request) );

    //  ACH: if we need to use multiple outbound application layer request packets, this will need to change
    _request.ctrl.first       = 1;
    _request.ctrl.final       = 1;
    _request.ctrl.app_confirm = 0;
    _request.ctrl.unsolicited = 0;

    _request.ctrl.seq = ++_seqno;

    _request.func_code = (unsigned char)_request_function;

    while( !_out_object_blocks.empty() )
    {
        const ObjectBlock *ob = _out_object_blocks.front();
        _out_object_blocks.pop();

        ob->serialize(_request.buf + pos);
        pos += ob->getSerializedLen();

        delete ob;
    }

    _request_buf_len = pos;
}


void Application::eraseInboundObjectBlocks( void )
{
    while( !_in_object_blocks.empty() )
    {
        delete _in_object_blocks.front();  //  safe to delete null

        _in_object_blocks.pop();
    }
}


void Application::eraseOutboundObjectBlocks( void )
{
    while( !_out_object_blocks.empty() )
    {
        delete _out_object_blocks.front();

        _out_object_blocks.pop();
    }
}


void Application::getObjects( object_block_queue &ob_queue )
{
    while( !_in_object_blocks.empty() )
    {
        ob_queue.push(_in_object_blocks.front());

        _in_object_blocks.pop();
    }
}


void Application::resetLink( void )
{
    _transport.resetLink();
}


bool Application::isTransactionComplete( void ) const
{
    return _ioState == Complete || _ioState == Uninitialized || _ioState == Failed;
}


bool Application::isOneWay( void ) const
{
    bool retVal;

    switch( _request.func_code )
    {
        case RequestConfirm:
        case RequestDirectOpNoAck:
        case RequestFreezeClrNoAck:
        case RequestFreezeWTimeNoAck:
        case RequestImmedFreezeNoAck:
        {
            retVal = true;
            break;
        }

        default:
        {
            retVal = false;
            break;
        }
    }

    return retVal;
}


bool Application::errorCondition( void ) const
{
    //  make this return decent error codes, not just a bool
    return _ioState == Failed;
}


int Application::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    if( _transport.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                //  _request was initialized by DNPInterface::generate()
                _transport.initForOutput((unsigned char *)&_request, _request_buf_len + ReqHeaderSize, _dstAddr, _srcAddr);

                break;
            }

            case Input:
            {
                _transport.initForInput((unsigned char *)&_response);

                break;
            }

            case OutputAck:
            {
                generateAck(&_acknowledge, _response.ctrl.seq);

                _transport.initForOutput((unsigned char *)&_acknowledge, sizeof(_acknowledge), _dstAddr, _srcAddr);

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - unhandled state " << _ioState << " in Cti::Protocol::DNP::Application::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            case Failed:
            {
                //  eventually, we should respect the results from _transport.initForOutput and _transport.initForInput - they could fail, too
                retVal = NOTNORMAL;

                break;
            }
        }
    }

    if( !retVal )
    {
        retVal = _transport.generate(xfer);
    }

    return retVal;
}


int Application::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;

    if( retVal = _transport.decode(xfer, status) )
    {
        if( ++_comm_errors < gDNPInternalRetries )  // CommRetries )
        {
            _ioState = _retryState;
            retVal = NoError;
        }
        else
        {
            _ioState    = Failed;
            _retryState = Failed;
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
                if( isOneWay() )
                {
                    _ioState    = Complete;
                    _retryState = Complete;
                }
                else
                {
                    _ioState    = Input;
                    //  leave _retryState on Output, so we re-output on error
                    //  _retryState = _ioState;
                }

                break;
            }

            case Input:
            {
                //  rudimentary bounds check on our input
                if( _transport.getInputSize() >= RspHeaderSize )
                {
                    _response_buf_len = _transport.getInputSize() - RspHeaderSize;

                    processInput();

                    if( _response.ctrl.app_confirm )
                    {
                        _ioState    = OutputAck;
                        _retryState = _ioState;
                    }
                    else if( !_response.ctrl.final )
                    {
                        _ioState    = Input;
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
                    _response_buf_len = 0;
                    _ioState    = Failed;
                    _retryState = _ioState;
                }

                break;
            }

            case OutputAck:
            {
                //  _response is untouched between Input and OutputAck, so this is still good data
                if( !_response.ctrl.final )
                {
                    _ioState    = Input;
                    _retryState = _ioState;
                }
                else
                {
                    _ioState    = Complete;
                    _retryState = _ioState;
                }

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - unknown state(" << _ioState << ") in Application::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                _ioState    = Failed;
                _retryState = _ioState;
            }
        }
    }

    return retVal;
}


void Application::generateAck( acknowledge_t *ack_packet, unsigned char seq )
{
    memset( ack_packet, 0, sizeof(*ack_packet) );

    ack_packet->func_code = RequestConfirm;

    ack_packet->ctrl.seq         = seq;

    ack_packet->ctrl.first       = 1;
    ack_packet->ctrl.final       = 1;

    ack_packet->ctrl.app_confirm = 0;
    ack_packet->ctrl.unsolicited = 0;
}

}
}
}

