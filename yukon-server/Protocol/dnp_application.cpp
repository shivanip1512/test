#include "precompiled.h"

#include "dnp_application.h"
#include "exceptions.h"

#include "logger.h"
#include "numstr.h"

using std::endl;
using std::string;

namespace Cti::Protocols::DNP {

ApplicationLayer::ApplicationLayer() :
    _request_function(RequestConfirm),
    _seqno(0),
    _appState(Uninitialized),
    _comm_errors(0),
    _config(0)
{
    memset( &_request,     0, sizeof(request_t) );
    memset( &_response,    0, sizeof(response_t) );
    memset( &_acknowledge, 0, sizeof(acknowledge_t) );
}

void ApplicationLayer::setConfigData( const config_data* config )
{
    _config = config;
}

void ApplicationLayer::setLoopback()
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _appState       = Loopback;
    _errorCondition = ClientErrors::None;
    _comm_errors = 0;

    //  setLoopback(), setCommand(), and initUnsolicited() are the only places where _iin is cleared
    _iin.reset();
}

void ApplicationLayer::setCommand( FunctionCode fc )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _request_function = fc;

    _appState       = SendRequest;
    _errorCondition = ClientErrors::None;
    _comm_errors = 0;

    //  setLoopback(), setCommand(), and initUnsolicited() are the only places where _iin is cleared
    _iin.reset();
}


void ApplicationLayer::setCommand( FunctionCode fc, ObjectBlockPtr dob )
{
    setCommand(fc);

    _out_object_blocks.push_back(std::move(dob));
}


void ApplicationLayer::setCommand( FunctionCode fc, std::vector<ObjectBlockPtr> dobs )
{
    setCommand(fc);

    for( auto &dob : dobs )
    {
        _out_object_blocks.push_back(std::move(dob));
    }
}


void ApplicationLayer::initUnsolicited( void )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _appState       = RecvUnsolicited;
    _errorCondition = ClientErrors::None;
    _comm_errors = 0;

    //  setLoopback(), setCommand(), and initUnsolicited() are the only places where _iin is cleared
    _iin.reset();
}


void ApplicationLayer::processResponse( void )
{
    if( gDNPVerbose )
    {
        Cti::FormattedList itemList;

        itemList.add("_response.ctrl.func_code")   << _response.func_code;

        itemList << "---";
        itemList.add("_response.ctrl.first")       << _response.ctrl.first;
        itemList.add("_response.ctrl.final")       << _response.ctrl.final;
        itemList.add("_response.ctrl.app_confirm") << _response.ctrl.app_confirm;
        itemList.add("_response.ctrl.unsolicited") << _response.ctrl.unsolicited;
        itemList.add("_response.ctrl.seq")         << _response.ctrl.seq;

        itemList << "---";
        itemList.add("_response.ind.broadcast")             << _response.ind.broadcast;
        itemList.add("_response.ind.already_executing")     << _response.ind.already_executing;
        itemList.add("_response.ind.config_corrupt")        << _response.ind.config_corrupt;
        itemList.add("_response.ind.no_func_code_support")  << _response.ind.no_func_code_support;
        itemList.add("_response.ind.event_buffer_overflow") << _response.ind.event_buffer_overflow;
        itemList.add("_response.ind.class_1_events")        << _response.ind.class_1_events;
        itemList.add("_response.ind.class_2_events")        << _response.ind.class_2_events;
        itemList.add("_response.ind.class_3_events")        << _response.ind.class_3_events;
        itemList.add("_response.ind.device_trouble")        << _response.ind.device_trouble;
        itemList.add("_response.ind.need_time")             << _response.ind.need_time;
        itemList.add("_response.ind.object_unknown")        << _response.ind.object_unknown;
        itemList.add("_response.ind.parameter_error")       << _response.ind.parameter_error;
        itemList.add("_response.ind.reserved")              << _response.ind.reserved;
        itemList.add("_response.ind.device_restart")        << _response.ind.device_restart;

        std::ostringstream data;
        data << endl;
        data << endl <<"data: ";
        data << endl << std::hex << std::setfill('0');

        for( int i = 0; i < _response.buf_len; i++ )
        {
            data << std::setw(2) << (int)_response.buf[i] <<" ";

            if( !(i % 20) && i )
            {
                data << endl;
            }
        }

        CTILOG_INFO(dout,
                itemList <<
                data
                );
    }

    //  ACH:  if class_1_events || class_2_events || class_3_events, we need to do something...  pass it up to the protocol layer, eh?

    if( ! _iin.has_value() )
    {
        _iin = _response.ind;
    }
    else
    {
        //  OR'ing them all together should catch all of the interesting indications from all frames
        _iin->raw |= _response.ind.raw;
    }

    for( auto &ob : restoreObjectBlocks(_response.buf, _response.buf_len) )
    {
        if( ob )
        {
            if( _response.func_code == ResponseUnsolicited )
            {
                ob->setUnsolicited();
            }

            _in_object_blocks.push_back(std::move(ob));
        }
    }
}


std::vector<std::unique_ptr<ObjectBlock>> ApplicationLayer::restoreObjectBlocks(const unsigned char *buf, const unsigned len)
{
    std::vector<std::unique_ptr<ObjectBlock>> blocks;

    int processed = 0;

    while( processed < len )
    {
        auto tmpOB = std::make_unique<ObjectBlock>();

        processed += tmpOB->restore(buf + processed, len - processed);

        blocks.push_back(std::move(tmpOB));
    }

    return blocks;
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

    for( const auto& ob : _out_object_blocks )
    {
        if( ob )
        {
            ob->serialize(_request.buf + pos);
            pos += ob->getSerializedLen();
        }
        else
        {
            CTILOG_ERROR(dout, "ob null");
        }
    }

    _out_object_blocks.clear();

    _request.buf_len = pos;
}

void ApplicationLayer::setSequenceNumber(int seqNbr)
{
    _seqno = seqNbr;
}

void ApplicationLayer::initForSlaveOutput( void )
{
    _appState    = SendFirstResponse;
    _comm_errors = 0;

    unsigned pos = 0;

    memset( &_response, 0, sizeof(_response) );

    //  ACH: if we need to use multiple outbound application layer request packets, this will need to change

    _response.ctrl.first       = 1;
    _response.ctrl.final       = 1;
    _response.ctrl.app_confirm = 0;
    _response.ctrl.unsolicited = 0;
    _response.ind.broadcast      = 0;
    _response.ind.class_1_events = 0;
    _response.ind.class_2_events = 0;
    _response.ind.class_3_events = 0;
    _response.ctrl.seq = _seqno;

    _response.func_code = DNP::ApplicationLayer::ResponseResponse;

    for( const auto& ob : _out_object_blocks )
    {
        if( ob )
        {
            ob->serialize(_response.buf + pos);
            pos += ob->getSerializedLen();
        }
        else
        {
            CTILOG_ERROR(dout, "ob null");
        }
    }

    _out_object_blocks.clear();

    _response.buf_len = pos;
}

void ApplicationLayer::setInternalIndications_FunctionCodeUnsupported()
{
    _response.ind.no_func_code_support = 1;
}

void ApplicationLayer::completeSlave( void )
{
    _appState = Complete;
}


void ApplicationLayer::eraseInboundObjectBlocks( void )
{
    _in_object_blocks.clear();
}


void ApplicationLayer::eraseOutboundObjectBlocks( void )
{
    _out_object_blocks.clear();
}


void ApplicationLayer::getObjects( object_block_queue &ob_queue )
{
    while( ! _in_object_blocks.empty() )
    {
        ob_queue.push_back(std::move(_in_object_blocks.front()));
        _in_object_blocks.pop_front();
    }
}


string ApplicationLayer::getInternalIndications( void ) const
{
    string iin;

    if( _iin && _iin->raw )
    {
        iin += "Internal indications:\n";

        if( _iin->broadcast )               iin += "Broadcast message received\n";
        if( _iin->class_1_events )          iin += "Class 1 data available\n";
        if( _iin->class_2_events )          iin += "Class 2 data available\n";
        if( _iin->class_3_events )          iin += "Class 3 data available\n";
        if( _iin->need_time )               iin += "Time synchronization needed\n";
        if( _iin->local_control )           iin += "Some digital output points in local mode - control disabled\n";
        if( _iin->device_trouble )          iin += "Device trouble (see device spec for details)\n";
        if( _iin->device_restart )          iin += "Device restart\n";

        if( _iin->no_func_code_support )    iin += "Function code not implemented\n";
        if( _iin->object_unknown )          iin += "Requested objects unknown\n";
        if( _iin->parameter_error )         iin += "Parameter error\n";
        if( _iin->event_buffer_overflow )   iin += "Event buffers have overflowed\n";
        if( _iin->already_executing )       iin += "Request already executing\n";
        if( _iin->config_corrupt )          iin += "DNP configuration is corrupt\n";
    }

    return iin;
}

YukonError_t ApplicationLayer::getIINErrorCode() const
{
    if( _iin )
    {
        if( _iin->no_func_code_support ) return ClientErrors::FunctionCodeNotImplemented;
        if( _iin->object_unknown )       return ClientErrors::UnknownObject;
        if( _iin->parameter_error )      return ClientErrors::ParameterError;
        if( _iin->already_executing )    return ClientErrors::OperationAlreadyExecuting;
    }

    return ClientErrors::None;
}

bool ApplicationLayer::hasInternalIndications() const
{
    return _iin.has_value();
}

bool ApplicationLayer::hasDeviceRestarted() const
{
    return _iin && _iin->device_restart;
}

bool ApplicationLayer::needsTime() const
{
    return _config && _config->enableDnpTimesyncs && _iin && _iin->need_time;
}

bool ApplicationLayer::isTransactionComplete( void ) const
{
    return _appState == Complete || _appState == Uninitialized;
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


YukonError_t ApplicationLayer::errorCondition( void ) const
{
    return _errorCondition;
}


YukonError_t ApplicationLayer::generate( TransportLayer &_transport )
{
    switch( _appState )
    {
        case Loopback:
        {
            return _transport.initLoopback();
        }

        case SendFirstResponse:
        {
            return _transport.initForOutput((unsigned char *)&_response, _response.buf_len + RspHeaderSize);
        }
        case SendResponse:
        {
            return ClientErrors::None;
        }
        case SendRequest:
        {
            //  _request was initialized by DNPInterface::generate()
            return _transport.initForOutput((unsigned char *)&_request, _request.buf_len + ReqHeaderSize);
        }

        case RecvUnsolicited:
        case RecvResponse:
        {
            return _transport.initForInput((unsigned char *)&_response, BufferSize);
        }

        case SendUnexpectedConfirm:
        case SendConfirm:
        {
            generateAck(&_acknowledge, _response.ctrl);

            return _transport.initForOutput((unsigned char *)&_acknowledge, sizeof(_acknowledge));
        }

        default:
        {
            CTILOG_ERROR(dout, "unhandled state "<< _appState);
            _appState = Complete;

        }
        case Complete:
        {
            return ClientErrors::Abnormal;
        }
    }
}


YukonError_t ApplicationLayer::decode( TransportLayer &_transport )
{
    if( _transport.errorCondition() )
    {
        if( ! _config )
        {
            throw MissingConfigException();
        }

        if( ++_comm_errors <= _config->internalRetries )  // CommRetries )
        {
            if( _appState == RecvResponse )
            {
                //  re-output on error
                _appState = SendRequest;
            }
        }
        else
        {
            _appState = Complete;
            _errorCondition = ClientErrors::Abnormal;
        }

        /*
        * Returning an Abnormal error will prevent the portfield comm status counter from resetting to 0 inappropriately
        * while not affecting anything else.
        */
        return ClientErrors::Abnormal;
    }

    switch( _appState )
    {
        case Loopback:
        {
            _appState = Complete;

            break;
        }

        case SendFirstResponse:
        {
            _appState = SendResponse;
        }
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
                processResponse();

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
                _appState = Complete;
                _errorCondition = ClientErrors::Abnormal;
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
            CTILOG_ERROR(dout, "unknown state ("<< _appState <<")");

            _appState = Complete;
            _errorCondition = ClientErrors::Abnormal;
        }
    }

    return ClientErrors::None;
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

