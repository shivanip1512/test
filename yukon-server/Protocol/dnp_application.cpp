#include "precompiled.h"

#include "dnp_application.h"
#include "exceptions.h"

#include "logger.h"
#include "numstr.h"

using std::endl;
using std::string;

namespace Cti {
namespace Protocols {
namespace DNP {

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

    _iin.raw = 0;
}

void ApplicationLayer::setConfigData( const config_data* config )
{
    _config = config;
}

void ApplicationLayer::setCommand( FunctionCode fc )
{
    eraseInboundObjectBlocks();
    eraseOutboundObjectBlocks();

    _request_function = fc;

    _appState       = SendRequest;
    _errorCondition = ClientErrors::None;
    _comm_errors = 0;

    //  this and initUnsolicited() are the only places where _iin is cleared
    _iin.raw = 0;
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

    //  this and setCommand() are the only places where _iin is cleared
    _iin.raw = 0;
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
        itemList.add("_response.ind.all_stations") << _response.ind.all_stations;
        itemList.add("_response.ind.already_exec") << _response.ind.already_exec;
        itemList.add("_response.ind.bad_config  ") << _response.ind.bad_config;
        itemList.add("_response.ind.bad_function") << _response.ind.bad_function;
        itemList.add("_response.ind.buf_overflow") << _response.ind.buf_overflow;
        itemList.add("_response.ind.class_1")      << _response.ind.class_1;
        itemList.add("_response.ind.class_2")      << _response.ind.class_2;
        itemList.add("_response.ind.class_3")      << _response.ind.class_3;
        itemList.add("_response.ind.dev_trouble ") << _response.ind.dev_trouble;
        itemList.add("_response.ind.need_time")    << _response.ind.need_time;
        itemList.add("_response.ind.obj_unknown ") << _response.ind.obj_unknown;
        itemList.add("_response.ind.out_of_range") << _response.ind.out_of_range;
        itemList.add("_response.ind.reserved")     << _response.ind.reserved;
        itemList.add("_response.ind.restart")      << _response.ind.restart;

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

    //  ACH:  if class_1 || class_2 || class_3, we need to do something...  pass it up to the protocol layer, eh?

    //  OR'ing them all together should catch all of the interesting indications from all frames
    _iin.raw |= _response.ind.raw;

    for( auto &ob : restoreObjectBlocks(_response.buf, _response.buf_len) )
    {
        if( _response.func_code == ResponseUnsolicited )
        {
            ob->setUnsolicited();
        }

        _in_object_blocks.push_back(std::move(ob));
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

    while( !_out_object_blocks.empty() )
    {
        const auto &ob = _out_object_blocks.front();

        ob->serialize(_request.buf + pos);
        pos += ob->getSerializedLen();

        _out_object_blocks.pop_front();
    }

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
    _response.ind.all_stations = 0;
    _response.ind.class_1 = 0;
    _response.ind.class_2 = 0;
    _response.ind.class_3 = 0;
    _response.ctrl.seq = _seqno;

    _response.func_code = DNP::ApplicationLayer::ResponseResponse;

    while( !_out_object_blocks.empty() )
    {
        const auto &ob = _out_object_blocks.front();

        ob->serialize(_response.buf + pos);
        pos += ob->getSerializedLen();

        _out_object_blocks.pop_front();
    }

    _response.buf_len = pos;
}

void ApplicationLayer::completeSlave( void )
{
    _appState = Complete;
}


void ApplicationLayer::eraseInboundObjectBlocks( void )
{
    std::swap(_in_object_blocks, decltype(_in_object_blocks)());
}


void ApplicationLayer::eraseOutboundObjectBlocks( void )
{
    std::swap(_out_object_blocks, decltype(_out_object_blocks)());
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

    if( _iin.raw )          iin += "Internal indications:\n";

    if( _iin.all_stations ) iin += "Broadcast message received\n";
    if( _iin.class_1      ) iin += "Class 1 data available\n";
    if( _iin.class_2      ) iin += "Class 2 data available\n";
    if( _iin.class_3      ) iin += "Class 3 data available\n";
    if( _iin.need_time    ) iin += "Time synchronization needed\n";
    if( _iin.local        ) iin += "Some digital output points in local mode - control disabled\n";
    if( _iin.dev_trouble  ) iin += "Device trouble (see device spec for details)\n";
    if( _iin.restart      ) iin += "Device restart\n";

    if( _iin.bad_function ) iin += "Function code not implemented\n";
    if( _iin.obj_unknown  ) iin += "Requested objects unknown\n";
    if( _iin.out_of_range ) iin += "Request parameters out of range\n";
    if( _iin.buf_overflow ) iin += "Event buffers have overflowed\n";
    if( _iin.already_exec ) iin += "Request already executing\n";
    if( _iin.bad_config   ) iin += "DNP configuration is corrupt\n";

    return iin;
}


bool ApplicationLayer::hasDeviceRestarted() const
{
    return _iin.restart;
}

bool ApplicationLayer::needsTime() const
{
    return _config && _config->enableDnpTimesyncs && _iin.need_time;
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
        case SendFirstResponse:
        {
            _transport.initForOutput((unsigned char *)&_response, _response.buf_len + RspHeaderSize);
        }
        case SendResponse:
        {
            break;
        }
        case SendRequest:
        {
            //  _request was initialized by DNPInterface::generate()
            _transport.initForOutput((unsigned char *)&_request, _request.buf_len + ReqHeaderSize);

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

            _transport.initForOutput((unsigned char *)&_acknowledge, sizeof(_acknowledge));

            break;
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

    return ClientErrors::None;
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
}
}

