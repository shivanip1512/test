#include "precompiled.h"

#include "yukon.h"
#include "logger.h"
#include "utility.h"
#include "prot_klondike.h"

#include "std_helper.h"

using namespace std;

namespace Cti {
namespace Protocols {

const KlondikeProtocol::command_state_map_t KlondikeProtocol::_command_states;

long long KlondikeProtocol::queue_entry_t::global_id;


KlondikeProtocol::KlondikeProtocol() :
    _wrap(&_idlc_wrap),
    _io_state(IO_Invalid),
    _error(Error_None),
    _loading_device_queue(false),
    _reading_device_queue(false),
    _device_queue_sequence(numeric_limits<unsigned int>::max()),
    _device_queue_entries_available(numeric_limits<unsigned int>::max()),
    _read_toggle(0x00),
    _dtran_queue_entry(byte_buffer_t(), 0, 0, 0, 0)
{
    _device_status.as_ushort = 0;
    _current_command.command = Command_Invalid;
    setAddresses(DefaultSlaveAddress, DefaultMasterAddress);
}

KlondikeProtocol::~KlondikeProtocol()
{
    _wrap = 0;      // we don't delete this: zeroing out to make lint happy...
}

KlondikeProtocol::command_state_map_t::command_state_map_t()
{
    //  this should be rewritten to allow command codes to be repeated within a command's state machine

    (*this)[Command_Raw      ].push_back(CommandCode_Null);  //  unused

    (*this)[Command_Loopback ].push_back(CommandCode_CheckStatus);

    (*this)[Command_LoadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_LoadQueue].push_back(CommandCode_WaitingQueueWrite);

    (*this)[Command_ReadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_ReadQueue].push_back(CommandCode_ReplyQueueRead);
    (*this)[Command_ReadQueue].push_back(CommandCode_ReplyQueueRead);

    (*this)[Command_TimeSync ].push_back(CommandCode_TimeSyncCCU);

    (*this)[Command_TimeRead ].push_back(CommandCode_ConfigurationMemoryRead);

    (*this)[Command_DirectTransmission].push_back(CommandCode_DirectMessageRequest);

    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableClear);
    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableWrite);
}


bool KlondikeProtocol::nextCommandState()
{
    command_state_map_t::const_iterator itr = _command_states.find(_current_command.command);

    //  find our proposed next state
    if( itr != _command_states.end() && _current_command.state < itr->second.size() )
    {
        _current_command.command_code = itr->second[_current_command.state];
        _current_command.state++;

        return commandStateValid();  //  check if we have a command to send
    }
    else
    {
        if( _current_command.command == Command_ReadQueue )
        {
            _reading_device_queue = false;
        }

        if( _current_command.command == Command_LoadQueue )
        {
            _loading_device_queue = false;
        }

        _current_command.command_code = CommandCode_Null;

        return false;  //  we don't have a command to send
    }
}


bool KlondikeProtocol::commandStateValid()
{
    bool command_valid = true;

    //  this should be rewritten to allow command codes to be repeated within a command's state machine
    if( _current_command.command == Command_LoadQueue )
    {
        if( _current_command.command_code == CommandCode_CheckStatus
            && _waiting_requests.empty() )
        {
            CTILOG_ERROR(dout, "Command_LoadQueue/CommandCode_CheckStatus : _waiting_requests.empty()");

            command_valid = false;
        }

        if( _current_command.command_code == CommandCode_WaitingQueueWrite
            && _device_queue_entries_available == 0 )
        {
            CTILOG_ERROR(dout, "Command_LoadQueue/CommandCode_CheckStatus : _device_queue_entries_available == 0");

            command_valid = false;
        }

        _loading_device_queue = command_valid;
    }

    if( _current_command.command      == Command_ReadQueue &&
        _current_command.command_code == CommandCode_ReplyQueueRead )
    {
        if( !_device_status.response_buffer_has_marked_data &&
            !_device_status.response_buffer_has_unmarked_data  )
        {
            if( !_device_status.transmit_buffer_has_data &&
                !_device_status.plc_transmitting_buffer_message &&
                !_remote_requests.empty() )
            {
                CTILOG_ERROR(dout, "Command_ReadQueue/CommandCode_ReplyQueueRead : device reports empty - purging _remote_requests");

                remote_work_t::iterator remote_itr = _remote_requests.begin(),
                                        remote_end = _remote_requests.end();

                while( remote_itr != remote_end )
                {
                    CTILOG_ERROR(dout, "Command_LoadRoutes/CommandCode_RoutingTableWrite : lost queue entry "<< remote_itr->first);

                    queue_entry_t &failed_entry = remote_itr->second;

                    _plc_results.push_back(
                        queue_result_t(
                            failed_entry.requester,
                            Error_QueueEntryLost,
                            ::time(0),
                            byte_buffer_t()));

                    remote_itr = _remote_requests.erase(remote_itr);
                }
            }

            command_valid = false;
        }
    }

    if( _current_command.command      == Command_LoadRoutes &&
        _current_command.command_code == CommandCode_RoutingTableWrite )
    {
        if( _routes.empty() )
        {
            CTILOG_ERROR(dout, "Command_LoadRoutes/CommandCode_RoutingTableWrite : _routes.empty()");

            command_valid = false;
        }
    }

    if( _current_command.command      == Command_DirectTransmission &&
        _current_command.command_code == CommandCode_DirectMessageRequest )
    {
        //  This is a horrible hack.  This should be overridden by something in a more formal state machine...  maybe this is a state we could skip instead, or something...
        //    A device initialization subset, kind of like IDLC's reset states.
        if( _device_queue_sequence == numeric_limits<unsigned int>::max() )
        {
            _current_command.command_code = CommandCode_CheckStatus;
            _current_command.state--;
        }
    }

    return command_valid;
}


void KlondikeProtocol::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _idlc_wrap.setAddress(_slaveAddress);
}


//  this is only called from recvCommRequest, so only one thread should be able to call this at once
YukonError_t KlondikeProtocol::setCommand( int command, byte_buffer_t payload, unsigned in_expected, unsigned priority, unsigned char stages, unsigned char dlc_parms )
{
    sync_guard_t guard(_sync);

    command_state_map_t::const_iterator itr = _command_states.find(static_cast<Command>(command));

    if( itr == _command_states.end() )
    {
        CTILOG_ERROR(dout, "invalid command ("<< command <<")");

        _current_command.command = Command_Invalid;
        _current_command.state = 0;
    }
    else
    {
        _current_command.command = itr->first;
        _current_command.state = 0;
    }

    //  these two are handled specially
    switch( _current_command.command )
    {
        case Command_Raw:
        {
            _raw_command.assign(payload.begin(), payload.end());

            break;
        }

        case Command_DirectTransmission:
        {
            _dtran_queue_entry = queue_entry_t(payload, priority, dlc_parms, stages, 0);
            _dtran_in_expected = in_expected;

            break;
        }
    }

    if( !nextCommandState() )
    {
        return ClientErrors::NoMethod;
    }

    _io_state = IO_Output;
    _error    = Error_None;
    _wrap_errors = 0;

    _wrap->init();

    return ClientErrors::None;
}


KlondikeProtocol::Command KlondikeProtocol::getCommand( void ) const
{
    return _current_command.command;
}


vector<KlondikeProtocol::queue_result_t> KlondikeProtocol::getQueuedResults()
{
    //  This would be perfect for a move constructor.
    vector<queue_result_t> results(_plc_results.begin(), _plc_results.end());

    _plc_results.clear();

    return results;
}


KlondikeProtocol::byte_buffer_t KlondikeProtocol::getDTranResult( void )
{
    return _dtran_result;
}


string KlondikeProtocol::describeCurrentStatus( void ) const
{
    ostringstream stream;

    sync_guard_t guard(_sync);

    if( _device_queue_entries_available != numeric_limits<unsigned int>::max() )  stream << "Queue entries available: " << _device_queue_entries_available << endl;
    if( _device_queue_sequence          != numeric_limits<unsigned int>::max() )  stream << "Current sequence number: " << hex << setw(4) << _device_queue_sequence << endl;

    stream << "Status (" << hex << setw(4) << _device_status.as_ushort << ")" << endl;

    if( _device_status.response_buffer_has_unmarked_data) stream << "  Response buffer has data        " << endl;
    if( _device_status.response_buffer_has_marked_data  ) stream << "  Response buffer has marked data " << endl;
    if( _device_status.response_buffer_full             ) stream << "  Response buffer full            " << endl;
    if( _device_status.transmit_buffer_has_data         ) stream << "  Transmit buffer has data        " << endl;
    if( _device_status.transmit_buffer_full             ) stream << "  Transmit buffer full            " << endl;
    if( _device_status.transmit_buffer_frozen           ) stream << "  Transmit buffer frozen          " << endl;
    if( _device_status.plc_transmitting_dtran_message   ) stream << "  PLC transmitting dtran message  " << endl;
    if( _device_status.plc_transmitting_buffer_message  ) stream << "  PLC transmitting buffer message " << endl;
    if( _device_status.time_sync_required               ) stream << "  Time sync required              " << endl;
    if( _device_status.broadcast_in_progress            ) stream << "  Broadcast in progress           " << endl;
    if( _device_status.reserved                         ) stream << "  Reserved bits set               " << endl;

    return stream.str();
}


YukonError_t KlondikeProtocol::generate( CtiXfer &xfer )
{
    sync_guard_t guard(_sync);

    if( _wrap->isTransactionComplete() )
    {
        switch( _io_state )
        {
            case IO_Output:  doOutput(_current_command.command_code);        break;
            case IO_Input:   doInput (_current_command.command_code, xfer);  break;
            default:
            {
                CTILOG_ERROR(dout, "unknown _io_state ("<< _io_state <<")");
            }
        }
    }

    return _wrap->generate(xfer);
}


void KlondikeProtocol::doOutput(CommandCode command_code)
{
    byte_buffer_t outbound;

    if( _current_command.command == Command_Raw )
    {
        _wrap->send(_raw_command);

        return;
    }

    //  first byte is always the command code
    outbound.push_back(command_code);

    switch( command_code )
    {
        //  no additional data
        case CommandCode_CheckStatus:
        {
            break;
        }

        case CommandCode_DirectMessageRequest:
        {
            outbound.push_back( _device_queue_sequence       & 0x00ff);
            outbound.push_back((_device_queue_sequence >> 8) & 0x00ff);

            outbound.push_back(_dtran_queue_entry.dlc_parms);
            outbound.push_back(_dtran_queue_entry.stages);

            outbound.push_back(_dtran_queue_entry.outbound.size());
            outbound.insert(outbound.end(), _dtran_queue_entry.outbound.begin(),
                                            _dtran_queue_entry.outbound.end());

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            outbound.push_back( _device_queue_sequence       & 0x00ff);
            outbound.push_back((_device_queue_sequence >> 8) & 0x00ff);

            int queue_count_pos = outbound.size();
            outbound.push_back(0);

            local_work_t::iterator waiting_itr = _waiting_requests.begin(),
                                   waiting_end = _waiting_requests.end();

            int processed = 0;

            while( waiting_itr != waiting_end && processed < _device_queue_entries_available )
            {
                if( waiting_itr->second.requester )
                {
                    if( outbound.size() + QueueEntryHeaderLength + waiting_itr->second.outbound.size() > _wrap->getMaximumPayload() )
                    {
                        break;
                    }
                    else
                    {
                        outbound.push_back(waiting_itr->second.priority);
                        outbound.push_back(waiting_itr->second.dlc_parms);
                        outbound.push_back(waiting_itr->second.stages);
                        outbound.push_back(waiting_itr->second.outbound.size());

                        outbound.insert(outbound.end(), waiting_itr->second.outbound.begin(),
                                                        waiting_itr->second.outbound.end());

                        _pending_requests.insert(make_pair(_device_queue_sequence + processed, waiting_itr->first));

                        processed++;
                    }

                    waiting_itr++;
                }
                else
                {
                    waiting_itr = _waiting_requests.erase(waiting_itr);
                }
            }

            outbound[queue_count_pos] = processed;

            break;
        }

        case CommandCode_ReplyQueueRead:
        {
            if( !_device_status.response_buffer_has_unmarked_data )
            {
                outbound.push_back(_read_toggle | BlockReadFlag_AckOnlyNoData);
            }
            else
            {
                outbound.push_back(_read_toggle);
            }

            break;
        }

        case CommandCode_RoutingTableClear:
        {
            outbound.push_back(0x00);  //  clear all slots

            break;
        }

        case CommandCode_RoutingTableWrite:
        {
            outbound.push_back(min<size_t>(_routes.size(), RoutesMaximum));

            route_list_t::iterator itr = _routes.begin();
            int route = 0;

            while( itr != _routes.end() && route <= RoutesMaximum )
            {
                outbound.push_back(route);
                outbound.push_back((itr->fixed  << 3) | itr->variable);
                outbound.push_back((itr->stages << 3) | itr->bus);

                route++;
                itr++;
            }

            break;
        }

        case CommandCode_TimeSyncCCU:
        {
            unsigned long now = currentTime();

            outbound.push_back(now & 0xff);   now >>= 8;
            outbound.push_back(now & 0xff);   now >>= 8;
            outbound.push_back(now & 0xff);   now >>= 8;
            outbound.push_back(now & 0xff);

            break;
        }

        /*
        case CommandCode_WaitingQueueFreeze:
        case CommandCode_WaitingQueueClear:

        case CommandCode_RoutingTableRead:
        case CommandCode_RoutingTableRequestAvailableSlots:

        case CommandCode_ConfigurationMemoryRead:
        case CommandCode_ConfigurationMemoryWrite:
        */

        default:
        {
            CTILOG_ERROR(dout, "unhandled command ("<< _current_command.command <<")");
        }
    }

    _wrap->send(outbound);
}


void KlondikeProtocol::doInput(CommandCode command_code, CtiXfer &xfer)
{
    _wrap->recv();

    if( command_code == CommandCode_DirectMessageRequest )
    {
        //  hack the timeout into the xfer before we pass it to _wrap->generate()
        xfer.setInTimeout((_dtran_queue_entry.stages + 1)
                          * (_dtran_queue_entry.outbound.size() + _dtran_in_expected)
                          * getPLCTiming(_dtran_queue_entry.protocol)
                          / 250);  //  the CtiXfer timeout is in 250ms chunks

        //  If the CCU is busy with a queue entry, he needs some extra time - we'll fudge this here, similar to portfield's "lto"
        if( !_remote_requests.empty() )
        {
            xfer.setInTimeout(xfer.getInTimeout() * 2);
        }
    }
}


unsigned KlondikeProtocol::getPLCTiming(PLCProtocols protocol)
{
    switch( protocol )
    {
        default:
        case PLCProtocol_Emetcon:   return 8 * 1000 / Emetcon_bps;
    }
}


bool KlondikeProtocol::responseExpected( CommandCode command_code )
{
    bool response = true;
/*
    switch( command )
    {
        case Command_OneWayExample:     response = false;   break;

        default:
            break;
    }
*/
    return response;
}


YukonError_t KlondikeProtocol::decode( CtiXfer &xfer, YukonError_t status )
{
    sync_guard_t guard(_sync);

    YukonError_t decode_status = _wrap->decode(xfer, status);

    if( _wrap->isTransactionComplete() )
    {
        if( _wrap->errorCondition() )
        {
            if( ++_wrap_errors > WrapErrorsMaximum )
            {
                _io_state = IO_Failed;

                processFailed();
            }
        }
        else
        {
            switch( _io_state )
            {
                case IO_Output:
                {
                    if( responseExpected(_current_command.command_code) )
                    {
                        //  next time through generate(), set xfer to read the input
                        _io_state = IO_Input;
                    }
                    else if( nextCommandState() )
                    {
                        _io_state = IO_Output;
                    }
                    else
                    {
                        _io_state = IO_Complete;
                    }

                    break;
                }

                case IO_Input:
                {
                    byte_buffer_t inbound;

                    _wrap->getInboundData(inbound);

                    //  process DTRAN response ACK/NACK
                    if( inbound.size() > WrapLengthMaximum )
                    {
                        CTILOG_ERROR(dout, "inbound.size() > WrapLengthMaximum ("<< inbound.size() <<" > "<< WrapLengthMaximum <<")");
                    }
                    else if( inbound.size() > 0 )
                    {
                        processResponse(inbound);
                    }

                    if( nextCommandState() )
                    {
                        _io_state = IO_Output;
                    }
                    else
                    {
                        _io_state = IO_Complete;
                    }
                }
            }
        }
    }

    if( _io_state == IO_Complete || _io_state == IO_Failed )
    {
        if( _current_command.command == Command_LoadQueue )  setLoadingDeviceQueue(false);
        if( _current_command.command == Command_ReadQueue )  setReadingDeviceQueue(false);
    }

    return decode_status;
}


void KlondikeProtocol::processResponse(const byte_buffer_t &inbound)
{
    byte_buffer_t::const_iterator       inbound_itr = inbound.begin();
    const byte_buffer_t::const_iterator inbound_end = inbound.end();

    if( distance(inbound_itr, inbound_end) < 4 )
    {
        return;
    }

    unsigned char response_command  = *inbound_itr++;
    unsigned char requested_command = *inbound_itr++;

    _device_status.as_bytes[0] = *inbound_itr++;
    _device_status.as_bytes[1] = *inbound_itr++;

    switch( _current_command.command_code )
    {
        case CommandCode_Null:
        {
            break;  //  used for raw commands - we don't know or care what the response is beyond the status bytes
        }
        case CommandCode_TimeSyncCCU:
        {
            //  if we succeed, the CCU-721 device code should queue a timesync to us
            if( response_command != CommandCode_ACK_NoData )
            {
                _error = Error_Unknown;  //  Error_TimesyncFailed;
            }

            break;
        }
        case CommandCode_RoutingTableWrite:
        case CommandCode_RoutingTableClear:
        {
            if( response_command != CommandCode_ACK_NoData )
            {
                _error = Error_Unknown;
            }

            break;
        }

        case CommandCode_CheckStatus:
        {
            if( response_command == CommandCode_NAK )
            {
                //  unused at the moment
                //  unsigned char nak_code = *inbound_itr++;

                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                if( distance(inbound_itr, inbound_end) < 3 )
                {
                    return;
                }

                _device_queue_entries_available = *inbound_itr++;
                _device_queue_sequence  = *inbound_itr++;
                _device_queue_sequence |= *inbound_itr++ << 8;
            }
            else
            {
                _error = Error_Unknown;
            }

            break;
        }

        case CommandCode_DirectMessageRequest:
        {
            _device_queue_sequence++;

            if( response_command == CommandCode_NAK )
            {
                if( distance(inbound_itr, inbound_end) < 1 )
                {
                    return;
                }

                unsigned char nak_code = *inbound_itr++;

                switch( nak_code )
                {
                    //  return Klondike error codes and let the CCU do the translation
                    case NAK_DirectTransmission_BusDisabled:            _error = Error_BusDisabled;             break;
                    case NAK_DirectTransmission_DTranBusy:              _error = Error_DTranBusy;               break;
                    case NAK_DirectTransmission_InvalidBus:             _error = Error_InvalidBus;              break;
                    case NAK_DirectTransmission_InvalidDLCType:         _error = Error_InvalidDLCType;          break;
                    case NAK_DirectTransmission_InvalidMessageLength:   _error = Error_InvalidMessageLength;    break;
                    case NAK_DirectTransmission_NoRoutes:               _error = Error_NoRoutes;                break;
                    case NAK_DirectTransmission_TransmitterOverheating: _error = Error_TransmitterOverheating;  break;

                    case NAK_DirectTransmission_InvalidSequence:
                    {
                        if( distance(inbound_itr, inbound_end) < 2 )
                        {
                            return;
                        }

                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        //  could do some trickery here and reset the command so it goes out again...

                        _error = Error_InvalidSequence;

                        break;
                    }

                    default:
                    {
                        CTILOG_ERROR(dout, "CommandCode_DirectMessageRequest : unhandled NAK code ("<< nak_code <<")");

                        _error = Error_Unknown;
                    }
                }
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error = Error_Unknown;
            }
            else
            {
                unsigned short sequence, snr;
                unsigned char  message_length;

                if( distance(inbound_itr, inbound_end) < 5 )
                {
                    return;
                }

                sequence  = *inbound_itr++;
                sequence |= *inbound_itr++ << 8;

                snr       = *inbound_itr++;
                snr      |= *inbound_itr++ << 8;

                message_length = *inbound_itr++;

                if( distance(inbound_itr, inbound_end) < message_length )
                {
                    return;
                }

                _dtran_result.assign(inbound_itr, inbound_itr + message_length);
            }

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            if( response_command == CommandCode_NAK )
            {
                if( distance(inbound_itr, inbound_end) < 1 )
                {
                    return;
                }

                switch( *inbound_itr++ )
                {
                    case NAK_LoadBuffer_QueueEntries:
                    {
                        if( distance(inbound_itr, inbound_end) < 3 )
                        {
                            return;
                        }

                        //  capture the expected sequence
                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        unsigned char number_rejected = *inbound_itr++;

                        if( distance(inbound_itr, inbound_end) < number_rejected * 3 )
                        {
                            return;
                        }

                        while( number_rejected-- > 0 )
                        {
                            unsigned short rejected_sequence;

                            rejected_sequence  = *inbound_itr++;
                            rejected_sequence |= *inbound_itr++ << 8;

                            unsigned char rejected_nak_code = *inbound_itr++;

                            pending_work_t::iterator pending_itr = _pending_requests.find(rejected_sequence);

                            if( pending_itr == _pending_requests.end() )
                            {
                                CTILOG_ERROR(dout, "CommandCode_WaitingQueueWrite : could not find rejected queue entry ("<< rejected_nak_code <<")");
                            }
                            else
                            {
                                local_work_t::iterator itr = _waiting_requests.find(pending_itr->second);

                                if( itr != _waiting_requests.end() )
                                {
                                    queue_entry_t &rejected_entry = itr->second;

                                    if( rejected_entry.resubmissions++ > QueueEntryResubmissionsMaximum
                                        || (rejected_nak_code != NAK_LoadBuffer_QueueFull &&
                                            rejected_nak_code != NAK_LoadBuffer_InvalidSequence) )
                                    {
                                        if( rejected_entry.requester )
                                        {
                                            KlondikeErrors error = Error_Unknown;

                                            switch( rejected_nak_code )
                                            {
                                                case NAK_LoadBuffer_BusDisabled:            error = Error_BusDisabled;               break;
                                                case NAK_LoadBuffer_InvalidBus:             error = Error_InvalidBus;                break;
                                                case NAK_LoadBuffer_InvalidDLCType:         error = Error_InvalidDLCType;            break;
                                                case NAK_LoadBuffer_InvalidMessageLength:   error = Error_InvalidMessageLength;      break;
                                                case NAK_LoadBuffer_NoRoutes:               error = Error_NoRoutes;                  break;
                                                default:
                                                {
                                                    CTILOG_ERROR(dout, "CommandCode_WaitingQueueWrite : unhandled NAK code ("<< rejected_nak_code <<")");
                                                }
                                            }

                                            _plc_results.push_back(
                                                queue_result_t(
                                                    rejected_entry.requester,
                                                    error,
                                                    ::time(0),
                                                    byte_buffer_t()));
                                        }

                                        _waiting_requests.erase(itr);
                                    }
                                }
                                else
                                {
                                    CTILOG_WARN(dout, "pending request not found in _waiting_requests, id " << pending_itr->second.id);
                                }

                                _pending_requests.erase(pending_itr);
                            }
                        }
                    }
                    default:
                    {

                        break;
                    }
                }
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                CTILOG_INFO(dout, "CommandCode_WaitingQueueWrite : CommandCode_ACK_NoData");
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                pending_work_t::iterator pending_itr = _pending_requests.begin(),
                                         pending_end = _pending_requests.end();

                if( distance(inbound_itr, inbound_end) < 2 )
                {
                    return;
                }

                unsigned accepted               = *inbound_itr++;
                _device_queue_entries_available = *inbound_itr++;

                while( (pending_itr != pending_end) && accepted )
                {
                    local_work_t::iterator itr = _waiting_requests.find(pending_itr->second);

                    if( itr != _waiting_requests.end() )
                    {
                        if( itr->second.requester )
                        {
                            _remote_requests.insert(make_pair(pending_itr->first, itr->second));
                        }

                        try
                        {
                            _waiting_requests.erase(itr);
                        }
                        catch( std::out_of_range &ex )
                        {
                            CTILOG_ERROR(dout,  "CommandCode_WaitingQueueWrite, caught out_of_range while deleting id " << pending_itr->second.id);
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "pending queue entry not found in _waiting_requests, id " << pending_itr->second.id);
                    }

                    _device_queue_sequence++;
                    accepted--;
                    pending_itr++;
                }

                if( accepted )
                {
                    CTILOG_ERROR(dout, "CommandCode_WaitingQueueWrite : accept > _pending_requests.size() in Cti::Protocol::Klondike::decode() ("<< accepted <<" > "<< _pending_requests.size() <<")");
                }

                _pending_requests.clear();

                //  should we try to load another back-to-back eventually?  what's the limit?  two full-boat messages?
                //  how should this flow?
                //  for now, we'll just load one and let the next one 'round do it again
                //  we'll only be able to do 5 reads/writes by the times the next 15 second load comes around anyway
            }

            _loading_device_queue = false;

            break;
        }

        case CommandCode_ReplyQueueRead:
        {
            if( response_command == CommandCode_NAK )
            {
                if( distance(inbound_itr, inbound_end) < 1 )
                {
                    return;
                }

                unsigned char nak_code = *inbound_itr++;

                CTILOG_ERROR(dout, "CommandCode_ReplyQueueRead : CommandCode_NAK ("<< nak_code <<")");

                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                if( distance(inbound_itr, inbound_end) < 1 )
                {
                    return;
                }

                _read_toggle = *inbound_itr++ ^ 0x01;  //  we'll echo this bit back to the CCU when we read or ack next

                CTILOG_ERROR(dout, "CommandCode_ReplyQueueRead : CommandCode_ACK_NoData");
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                if( distance(inbound_itr, inbound_end) < 2 )
                {
                    return;
                }

                _read_toggle = *inbound_itr++ ^ 0x01;  //  we'll echo this bit back to the CCU when we read or ack next

                unsigned queue_entries_read = *inbound_itr++;

                for( int entry = 0; entry < queue_entries_read; entry++ )
                {
                    try
                    {
                        //  queue_response_t's constructor increments the inbound iterator
                        queue_response_t q(inbound_itr,
                                           inbound_end);

                        {
                            Cti::StreamBuffer outLog;

                            outLog <<"read queue response (seq, timestamp, signal strength, result, message) ("<< setfill('0') << hex
                                   << setw(4) << q.sequence         <<", "
                                   << CtiTime(q.timestamp)          <<", "
                                   << setw(4) << q.signal_strength  <<", "
                                   << setw(2) << (unsigned)q.result <<", ";

                            byte_buffer_t::iterator message_itr = q.message.begin();

                            while( message_itr != q.message.end() )
                            {
                                outLog << setw(2) << (unsigned)*message_itr++;
                            }

                            outLog <<")";

                            CTILOG_INFO(dout, outLog);
                        }

                        remote_work_t::iterator remote_itr = _remote_requests.find(q.sequence);

                        if( remote_itr == _remote_requests.end() )
                        {
                            CTILOG_ERROR(dout, "unknown sequence ("<< hex << setw(4) << q.sequence <<") received");
                        }
                        else
                        {
                            if( remote_itr->second.requester )
                            {
                                _plc_results.push_back(
                                    queue_result_t(
                                        remote_itr->second.requester,
                                        Error_None,  //  we'll eventually need to plug an error code in here,
                                        q.timestamp, //    but right now, q.result is poorly defined
                                        q.message));
                            }

                            _remote_requests.erase(remote_itr);
                        }
                    }
                    catch( std::range_error &re )
                    {
                        CTILOG_EXCEPTION_ERROR(dout, re, "range error, restoring at position "<< inbound_itr - inbound.begin() <<"/"<< inbound.size());

                        return;
                    }
                }
            }

            break;
        }

        /*
        case CommandCode_WaitingQueueFreeze:
        case CommandCode_WaitingQueueRead:
        case CommandCode_WaitingQueueClear:

        case CommandCode_RoutingTableRead:
        case CommandCode_RoutingTableRequestAvailableSlots:

        case CommandCode_ConfigurationMemoryRead:
        case CommandCode_ConfigurationMemoryWrite:
        */

        default:
        {
            CTILOG_ERROR(dout, "unhandled command code ("<< _current_command.command_code <<")");
        }
    }
}


//  This command undoes anything we were just attempting to do - the wrap failed, we have to try again
void KlondikeProtocol::processFailed()
{
    switch( _current_command.command_code )
    {
        case CommandCode_WaitingQueueWrite:
        {
            //  these entries are still in _waiting_requests and will be tried again
            _pending_requests.clear();
            break;
        }
    }
}


bool KlondikeProtocol::isTransactionComplete( void ) const
{
    return _current_command.command == Command_Invalid ||
           _io_state                == IO_Complete     ||
           errorCondition();
}


bool KlondikeProtocol::errorCondition( void ) const
{
    return _io_state == IO_Failed;

}



KlondikeProtocol::KlondikeErrors KlondikeProtocol::errorCode() const
{
    return _error;
}



bool KlondikeProtocol::addQueuedWork(void *requester, const byte_buffer_t &payload, unsigned priority, unsigned char dlc_parms, unsigned char stages)
{
    sync_guard_t guard(_sync);

    queue_entry_t new_entry(payload, priority, dlc_parms, stages, requester);
    id_priority key = { new_entry.id, new_entry.priority };

    _waiting_requests.insert(make_pair(key, new_entry));

    return true;
}

bool KlondikeProtocol::removeQueuedWork(void *handle)
{
    sync_guard_t guard(_sync);

    local_work_t::iterator itr = _waiting_requests.begin();

    while( itr != _waiting_requests.end() )
    {
        if( itr->second.requester == handle )
        {
            //  this keeps us from propagating this into _remote_requests
            itr->second.requester = 0;

            return true;
        }
        else
        {
            ++itr;
        }
    }

    return false;
}

bool KlondikeProtocol::isLoadingDeviceQueue() const
{
    sync_guard_t guard(_sync);

    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _loading_device_queue;
}


bool KlondikeProtocol::setLoadingDeviceQueue(bool loading)
{
    sync_guard_t guard(_sync);

    return (_loading_device_queue = loading);
}


bool KlondikeProtocol::isReadingDeviceQueue() const
{
    sync_guard_t guard(_sync);

    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _reading_device_queue;
}


bool KlondikeProtocol::setReadingDeviceQueue(bool reading)
{
    sync_guard_t guard(_sync);

    return (_reading_device_queue = reading);
}


void KlondikeProtocol::getRequestStatus(request_statuses &waiting, request_statuses &pending, request_statuses &queued, request_statuses &completed) const
{
    sync_guard_t guard(_sync);

    request_status s;

    for each(const local_work_t::value_type &entry in _waiting_requests)
    {
        s.requester = entry.second.requester;
        s.priority  = entry.second.priority;
        s.queue_id  = 0;

        waiting.push_back(s);
    }

    for each(const pending_work_t::value_type &pending_request in _pending_requests)
    {
        if( const boost::optional<queue_entry_t> waiting_entry = mapFind(_waiting_requests, pending_request.second) )
        {
            s.requester = waiting_entry->requester;
            s.priority  = waiting_entry->priority;
            s.queue_id  = pending_request.first;

            pending.push_back(s);
        }
    }

    for each(const remote_work_t::value_type &remote_request in _remote_requests)
    {
        s.requester = remote_request.second.requester;
        s.priority  = remote_request.second.priority;
        s.queue_id  = remote_request.first;

        queued.push_back(s);
    }

    for each(const queue_result_t &result in _plc_results)
    {
        s.requester = result.requester;
        s.priority  = 0;
        s.queue_id  = 0;

        completed.push_back(s);
    }
}



bool KlondikeProtocol::hasRemoteWork() const
{
    sync_guard_t guard(_sync);

    return !_remote_requests.empty()
            || _device_status.response_buffer_has_unmarked_data
            || _device_status.response_buffer_has_marked_data;
}


struct store_max_priority
{
    unsigned max_priority;

    store_max_priority(unsigned base_priority) : max_priority(base_priority) { };

    template<class T>
    void operator()(T element)
    {
        max_priority = max(max_priority, element.second.priority);
    }
};


unsigned KlondikeProtocol::getRemoteWorkPriority() const
{
    sync_guard_t guard(_sync);

    store_max_priority store(QueueReadBasePriority);

    for_each(_remote_requests.begin(),
             _remote_requests.end(),
             store);

    return store.max_priority;
}


bool KlondikeProtocol::hasWaitingWork() const
{
    sync_guard_t guard(_sync);

    return !_waiting_requests.empty();
}


unsigned KlondikeProtocol::getWaitingWorkPriority() const
{
    sync_guard_t guard(_sync);

    unsigned priority = QueueWriteBasePriority;

    //  The first entry in the set will always have the highest priority
    if( !_waiting_requests.empty() )
    {
        priority = max(priority, _waiting_requests.begin()->second.priority);
    }

    return priority;
}


void KlondikeProtocol::addRoute(unsigned bus, unsigned fixed, unsigned variable, unsigned stages)
{
    route_entry_t route;

    route.bus      = bus;
    route.fixed    = fixed;
    route.variable = variable;
    route.stages   = stages;

    _routes.push_back(route);
}


void KlondikeProtocol::clearRoutes()
{
    _routes.clear();
}


void KlondikeProtocol::setWrap(Protocols::Wrap *wrap)
{
    _wrap = wrap;
}

long KlondikeProtocol::currentTime()
{
    return ::time(0);
}

}
}

