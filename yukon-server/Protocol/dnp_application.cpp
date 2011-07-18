#include "precompiled.h"

#include "dnp_application.h"

#include "prot_dnp.h"

#include "logger.h"
#include "numstr.h"

using std::endl;
using std::string;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

ApplicationLayer::ApplicationLayer() :
    _dstAddr(0),
    _srcAddr(0),
    _request_function(RequestConfirm),
    _seqno(0),
    _appState(Uninitialized),
    _comm_errors(0)
{
    memset( &_request,     0, sizeof(request_t) );
    memset( &_response,    0, sizeof(response_t) );
    memset( &_acknowledge, 0, sizeof(acknowledge_t) );

    _iin.raw = 0;
}

ApplicationLayer::ApplicationLayer(const ApplicationLayer &aRef)
{
    *this = aRef;
}

ApplicationLayer::~ApplicationLayer()
{
    eraseOutboundObjectBlocks();
    eraseInboundObjectBlocks();
}

ApplicationLayer &ApplicationLayer::operator=(const ApplicationLayer &aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}


void ApplicationLayer::setAddresses( unsigned short dstAddr, unsigned short srcAddr )
{
    _dstAddr = dstAddr;
    _srcAddr = srcAddr;

    _transport.setAddresses(_dstAddr, _srcAddr);
}


void ApplicationLayer::setOptions( int options )
{
    _transport.setOptions(options);
}


void ApplicationLayer::setCommand( FunctionCode fc )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _request_function = fc;

    _appState    = SendRequest;
    _comm_errors = 0;

    //  this and initUnsolicited() are the only places where _iin is cleared
    _iin.raw = 0;
}


void ApplicationLayer::initUnsolicited( void )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _appState    = RecvUnsolicited;
    _comm_errors = 0;

    //  this and setCommand() are the only places where _iin is cleared
    _iin.raw = 0;
}


void ApplicationLayer::addObjectBlock( const ObjectBlock *objBlock )
{
    _out_object_blocks.push(objBlock);
}


void ApplicationLayer::processInput( void )
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

        for( int i = 0; i < _response.buf_len; i++ )
        {
            dout << string(CtiNumStr(_response.buf[i]).hex().zpad(2)) << " ";

            if( !(i % 20) && i )
                dout << endl;
        }

        dout << endl << endl;
    }

    //  ACH:  if class_1 || class_2 || class_3, we need to do something...  pass it up to the protocol layer, eh?
    //  also, if need_time, do some time syncing, boieeeee

    int processed = 0;

    //  OR'ing them all together should catch all of the interesting indications from all frames
    _iin.raw |= _response.ind.raw;

    while( processed < _response.buf_len )
    {
        ObjectBlock *tmpOB = CTIDBG_new ObjectBlock;

        processed += tmpOB->restore(&(_response.buf[processed]), _response.buf_len - processed);

        _in_object_blocks.push(tmpOB);
    }
}


void ApplicationLayer::initForOutput( void )
{
    unsigned pos = 0;

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

    _request.buf_len = pos;
}

void ApplicationLayer::setSequenceNumber(int seqNbr)
{
    _seqno = seqNbr;
}

void ApplicationLayer::initForSlaveOutput( void )
{
    _appState    = SendResponse;
    _comm_errors = 0;

    unsigned pos = 0;

    memset( &_response, 0, sizeof(_response) );

    //  ACH: if we need to use multiple outbound application layer request packets, this will need to change

    _response.ctrl.first       = 1;
    _response.ctrl.final       = 1;
    _response.ctrl.app_confirm = 0;
    _response.ctrl.unsolicited = 0;
    _response.ind.all_stations = 1;
    _response.ind.class_1 = 1;
    _response.ind.class_2 = 1;
    _response.ind.class_3 = 1;
    _response.ctrl.seq = _seqno;

    _response.func_code = DNP::ApplicationLayer::ResponseResponse;

    while( !_out_object_blocks.empty() )
    {
        const ObjectBlock *ob = _out_object_blocks.front();
        _out_object_blocks.pop();

        ob->serialize(_response.buf + pos);
        pos += ob->getSerializedLen();

        delete ob;
    }

    _response.buf_len = pos;
}

void ApplicationLayer::completeSlave( void )
{
    _appState = Complete;
    _transport.setIoStateComplete();
}


void ApplicationLayer::eraseInboundObjectBlocks( void )
{
    while( !_in_object_blocks.empty() )
    {
        delete _in_object_blocks.front();  //  safe to delete null

        _in_object_blocks.pop();
    }
}


void ApplicationLayer::eraseOutboundObjectBlocks( void )
{
    while( !_out_object_blocks.empty() )
    {
        delete _out_object_blocks.front();

        _out_object_blocks.pop();
    }
}


void ApplicationLayer::getObjects( object_block_queue &ob_queue )
{
    while( !_in_object_blocks.empty() )
    {
        ob_queue.push(_in_object_blocks.front());

        _in_object_blocks.pop();
    }
}


string ApplicationLayer::getInternalIndications( void ) const
{
    string iin = "Internal indications:\n";

    if( _iin.all_stations ) iin += "Broadcast message received\n";
    if( _iin.class_1      ) iin += "Class 1 data available\n";
    if( _iin.class_2      ) iin += "Class 2 data available\n";
    if( _iin.class_3      ) iin += "Class 3 data available\n";
    if( _iin.need_time    ) iin += "Time synchronization needed\n";
    if( _iin.local        ) iin += "Some digital output points in local mode - control disabled\n";
    if( _iin.dev_trouble  ) iin += "Device trouble (see device spec for details)\n";
    if( _iin.restart      ) iin += "User application restarted\n";

    if( _iin.bad_function ) iin += "Function code not implemented\n";
    if( _iin.obj_unknown  ) iin += "Requested objects unknown\n";
    if( _iin.out_of_range ) iin += "Request parameters out of range\n";
    if( _iin.buf_overflow ) iin += "Event buffers have overflowed\n";
    if( _iin.already_exec ) iin += "Request already executing\n";
    if( _iin.bad_config   ) iin += "DNP configuration is corrupt\n";

    return iin;
}


void ApplicationLayer::resetLink( void )
{
    _transport.resetLink();
}


bool ApplicationLayer::isTransactionComplete( void ) const
{
    return _appState == Complete || _appState == Uninitialized || _appState == Failed;
}


bool ApplicationLayer::isOneWay( void ) const
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


bool ApplicationLayer::errorCondition( void ) const
{
    //  make this return decent error codes, not just a bool
    return _appState == Failed;
}


int ApplicationLayer::generate( CtiXfer &xfer )
{
    int retVal = NoError;

    if( _transport.isTransactionComplete() )
    {
        switch( _appState )
        {
            case SendResponse:
            {
                _transport.initForOutput((unsigned char *)&_response, _response.buf_len + RspHeaderSize, _dstAddr, _srcAddr);

                break;
            }
            case SendRequest:
            {
                //  _request was initialized by DNPInterface::generate()
                _transport.initForOutput((unsigned char *)&_request, _request.buf_len + ReqHeaderSize, _dstAddr, _srcAddr);

                break;
            }

            case RecvUnsolicited:
            case RecvResponse:
            {
                _transport.initForInput((unsigned char *)&_response, BufferSize);

                break;
            }

            case SendUnexpectedConfirm:
            case SendConfirm:
            {
                generateAck(&_acknowledge, _response.ctrl);

                _transport.initForOutput((unsigned char *)&_acknowledge, sizeof(_acknowledge), _dstAddr, _srcAddr);

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled state " << _appState << " in Cti::Protocol::DNP::Application::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            case Failed:
            {
                //  eventually, we should respect the results from _transport.initForOutput and _transport.initForInput - they could fail, too
                _appState = Failed;
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


int ApplicationLayer::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;

    if( retVal = _transport.decode(xfer, status) )
    {
        if( ++_comm_errors <= gDNPInternalRetries )  // CommRetries )
        {
            if( _appState == RecvResponse )
            {
                //  re-output on error
                _appState = SendRequest;
            }
        }
        else
        {
            _appState = Failed;
        }

        if( isDebugLudicrous() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if( _transport.isTransactionComplete() )
    {
        switch( _appState )
        {
            case SendResponse:
            {
                _appState = Complete;

                break;
            }

            case SendRequest:
            {
                if( isOneWay() )
                {
                    _appState = Complete;
                }
                else
                {
                    _appState = RecvResponse;
                }

                break;
            }

            case RecvUnsolicited:
            case RecvResponse:
            {
                //  rudimentary bounds check on our input
                if( _transport.getInputSize() >= RspHeaderSize )
                {
                    _response.buf_len = _transport.getInputSize() - RspHeaderSize;

                    //  add filtering for duplicate/unexpected/bad sequence packets
                    processInput();

                    if( _response.ctrl.app_confirm )
                    {
                        if( _response.ctrl.unsolicited && _appState == RecvResponse )
                        {
                            //  we weren't expecting an unsolicited -
                            //  acknowledge it so we can get back to business
                            _appState = SendUnexpectedConfirm;
                        }
                        else
                        {
                            _appState = SendConfirm;
                        }
                    }
                    else if( !_response.ctrl.final )
                    {
                        _appState = RecvResponse;
                    }
                    else
                    {
                        _appState = Complete;
                    }
                }
                else
                {
                    _response.buf_len = 0;
                    _appState = Failed;
                }

                break;
            }

            case SendUnexpectedConfirm:
            {
                //  get back to business - listen for the real response now
                _appState = RecvResponse;

                break;
            }

            case SendConfirm:
            {
                //  _response is untouched between Input and OutputAck, so this is still good data
                if( !_response.ctrl.final )
                {
                    _appState = RecvResponse;
                }
                else
                {
                    _appState = Complete;
                }

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unknown state(" << _appState << ") in Application::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                _appState = Failed;
            }
        }
    }

    return retVal;
}


void ApplicationLayer::generateAck( acknowledge_t *ack_packet, const control_header ctrl )
{
    memset( ack_packet, 0, sizeof(*ack_packet) );

    ack_packet->func_code = RequestConfirm;

    ack_packet->ctrl.seq         = ctrl.seq;

    ack_packet->ctrl.first       = 1;
    ack_packet->ctrl.final       = 1;

    ack_packet->ctrl.app_confirm = 0;
    ack_packet->ctrl.unsolicited = ctrl.unsolicited;
}

}
}
}

