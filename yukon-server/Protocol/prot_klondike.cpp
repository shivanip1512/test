/*-----------------------------------------------------------------------------*
*
* File:   prot_klondike
*
* Date:   2006-aug-08
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2008/10/31 15:55:35 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "utility.h"
#include "prot_klondike.h"

using namespace std;

namespace Cti       {
namespace Protocol  {

const Klondike::command_state_map_t Klondike::_command_states;


Klondike::Klondike() :
    _wrap(&_idlc_wrap),
    _io_state(IO_Invalid),
    _error(Error_None),
    _loading_device_queue(false),
    _reading_device_queue(false),
    _device_queue_sequence(numeric_limits<unsigned int>::max()),
    _device_queue_entries_available(numeric_limits<unsigned int>::max()),
    _read_toggle(0x00)
{
    _device_status.as_ushort = 0;
    _current_command.command = Command_Invalid;
    setAddresses(DefaultSlaveAddress, DefaultMasterAddress);
}

Klondike::Klondike(const Klondike &aRef)
{
    *this = aRef;
}

Klondike::~Klondike()
{
}

Klondike &Klondike::operator=(const Klondike &aRef)
{
    if( this != &aRef )
    {
        _masterAddress = aRef._masterAddress;
        _slaveAddress  = aRef._slaveAddress;
    }

    return *this;
}


Klondike::command_state_map_t::command_state_map_t()
{
    //  this should be rewritten to allow command codes to be repeated within a command's state machine

    (*this)[Command_Raw      ].push_back(CommandCode_Null);  //  unused

    (*this)[Command_Loopback ].push_back(CommandCode_CheckStatus);

    (*this)[Command_LoadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_LoadQueue].push_back(CommandCode_WaitingQueueWrite);

    (*this)[Command_ReadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_ReadQueue].push_back(CommandCode_ReplyQueueRead);

    (*this)[Command_TimeSync ].push_back(CommandCode_TimeSyncCCU);

    (*this)[Command_TimeRead ].push_back(CommandCode_ConfigurationMemoryRead);

    (*this)[Command_DirectTransmission].push_back(CommandCode_DirectMessageRequest);

    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableClear);
    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableWrite);
}


bool Klondike::nextCommandState()
{
    command_state_map_t::iterator itr = _command_states.find(_current_command.command);

    //  find our proposed next state
    if( itr != _command_states.end() && _current_command.state < itr->second.size() )
    {
        _current_command.command_code = itr->second[_current_command.state];
        _current_command.state++;

        return commandStateValid();  //  check if we have a command to send
    }
    else
    {
        _current_command.command_code = CommandCode_Null;

        return false;  //  we don't have a command to send
    }
}


bool Klondike::commandStateValid()
{
    bool command_valid = true;

    //  this should be rewritten to allow command codes to be repeated within a command's state machine
    if( _current_command.command == Command_LoadQueue )
    {
        if( _current_command.command_code == CommandCode_CheckStatus
            && _waiting_requests.empty() )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : Command_LoadQueue/CommandCode_CheckStatus : _waiting_requests.empty() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            command_valid = false;
        }

        if( _current_command.command_code == CommandCode_WaitingQueueWrite
            && _device_queue_entries_available == 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : Command_LoadQueue/CommandCode_CheckStatus : _device_queue_entries_available == 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            command_valid = false;
        }
    }

    if( _current_command.command      == Command_ReadQueue &&
        _current_command.command_code == CommandCode_ReplyQueueRead )
    {
        if( _remote_requests.empty() && !_device_status.response_buffer_has_data )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : Command_ReadQueue/CommandCode_ReplyQueueRead : _remote_requests.empty() && !_device_status.response_buffer_has_data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            command_valid = false;
        }
    }

    if( _current_command.command      == Command_LoadRoutes &&
        _current_command.command_code == CommandCode_RoutingTableWrite )
    {
        if( _routes.empty() )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : Command_LoadRoutes/CommandCode_RoutingTableWrite : _routes.empty() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

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


void Klondike::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _idlc_wrap.setAddress(_slaveAddress);
}


//  this is only called from recvCommRequest, so only one thread should be able to call this at once
int Klondike::setCommand( int command, byte_buffer_t payload, unsigned in_expected, unsigned priority, unsigned char stages, unsigned char dlc_parms )
{
    sync_guard_t guard(_sync);

    command_state_map_t::const_iterator itr = _command_states.find(static_cast<Command>(command));

    if( itr == _command_states.end() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid command (" << command << ") in Klondike::setCommand() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

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
            _dtran_queue_entry.outbound.assign(payload.begin(), payload.end());
            _dtran_queue_entry.dlc_parms = dlc_parms;
            _dtran_queue_entry.stages    = stages;
            _dtran_queue_entry.priority  = priority;
            _dtran_in_expected = in_expected;

            break;
        }
    }

    if( !nextCommandState() )
    {
        return NoMethod;
    }
    else
    {
        _io_state = IO_Output;
        _error    = Error_None;
        _wrap_errors = 0;

        _wrap->init();

        return NoError;
    }
}


Klondike::Command Klondike::getCommand( void ) const
{
    return _current_command.command;
}


void Klondike::getQueuedResults( queue<queue_result_t> &results )
{
    while( !_plc_results.empty() )
    {
        results.push(_plc_results.front());

        _plc_results.pop();
    }
}


Klondike::byte_buffer_t Klondike::getDTranResult( void )
{
    return _dtran_result;
}


string Klondike::describeCurrentStatus( void ) const
{
    ostringstream stream;

    sync_guard_t guard(_sync);

    if( _device_queue_entries_available != numeric_limits<unsigned int>::max() )  stream << "_device_queue_entries_available = " << _device_queue_entries_available << endl;
    if( _device_queue_sequence          != numeric_limits<unsigned int>::max() )  stream << "_device_queue_sequence          = " << hex << setw(4) << _device_queue_sequence << endl;

    stream << "_device_status.as_ushort        = " << hex << setw(4) << _device_status.as_ushort        << endl;

    if( _device_status.response_buffer_has_data        ) stream << "_device_status.response_buffer_has_data        " << endl;
    if( _device_status.response_buffer_has_marked_data ) stream << "_device_status.response_buffer_has_marked_data " << endl;
    if( _device_status.response_buffer_full            ) stream << "_device_status.response_buffer_full            " << endl;
    if( _device_status.transmit_buffer_has_data        ) stream << "_device_status.transmit_buffer_has_data        " << endl;
    if( _device_status.transmit_buffer_full            ) stream << "_device_status.transmit_buffer_full            " << endl;
    if( _device_status.transmit_buffer_frozen          ) stream << "_device_status.transmit_buffer_frozen          " << endl;
    if( _device_status.plc_transmitting_dtran_message  ) stream << "_device_status.plc_transmitting_dtran_message  " << endl;
    if( _device_status.plc_transmitting_buffer_message ) stream << "_device_status.plc_transmitting_buffer_message " << endl;
    if( _device_status.time_sync_required              ) stream << "_device_status.time_sync_required              " << endl;
    if( _device_status.broadcast_in_progress           ) stream << "_device_status.broadcast_in_progress           " << endl;
    if( _device_status.reserved                        ) stream << "_device_status.reserved                        " << endl;

    return stream.str();
}


int Klondike::generate( CtiXfer &xfer )
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::generate() : unknown _io_state (" << _io_state << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return _wrap->generate(xfer);
}


void Klondike::doOutput(CommandCode command_code)
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

            for( ; waiting_itr != waiting_end && processed < _device_queue_entries_available; waiting_itr++ )
            {
                if( outbound.size() + QueueEntryHeaderLength + waiting_itr->outbound.size() > _wrap->getMaximumPayload() )
                {
                    break;
                }
                else
                {
                    outbound.push_back(waiting_itr->priority);
                    outbound.push_back(waiting_itr->dlc_parms);
                    outbound.push_back(waiting_itr->stages);
                    outbound.push_back(waiting_itr->outbound.size());

                    outbound.insert(outbound.end(), waiting_itr->outbound.begin(),
                                                    waiting_itr->outbound.end());

                    _pending_requests.insert(make_pair(_device_queue_sequence, waiting_itr));

                    processed++;
                }
            }

            outbound[queue_count_pos] = processed;

            break;
        }

        case CommandCode_ReplyQueueRead:
        {
            outbound.push_back(_read_toggle);

            break;
        }

        case CommandCode_RoutingTableClear:
        {
            outbound.push_back(0x00);  //  clear all slots

            break;
        }

        case CommandCode_RoutingTableWrite:
        {
            outbound.push_back(min((unsigned)_routes.size(), (unsigned)32));

            route_list_t::iterator itr = _routes.begin();
            int route = 0;

            while( itr != _routes.end() && route <= RoutesMaximum )
            {
                outbound.push_back(route);
                outbound.push_back((itr->fixed  << 5) | itr->variable);
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled command (" << _current_command.command << ") in Cti::Protocol::Klondike::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    _wrap->send(outbound);
}


void Klondike::doInput(CommandCode command_code, CtiXfer &xfer)
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


unsigned Klondike::getPLCTiming(PLCProtocols protocol)
{
    switch( protocol )
    {
        default:
        case PLCProtocol_Emetcon:   return 8 * 1000 / Emetcon_bps;
    }
}


bool Klondike::responseExpected( CommandCode command_code )
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


int Klondike::decode( CtiXfer &xfer, int status )
{
    sync_guard_t guard(_sync);

    int decode_status = _wrap->decode(xfer, status);

    if( _wrap->isTransactionComplete() )
    {
        if( _wrap->errorCondition() )
        {
            if( ++_wrap_errors > WrapErrorsMaximum )
            {
                _io_state = IO_Failed;
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::decode() : inbound.size() > WrapLengthMaximum ("
                                          << inbound.size() << " > " << WrapLengthMaximum
                                          << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    else
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


void Klondike::processResponse(const byte_buffer_t &inbound)
{
    byte_buffer_t::const_iterator inbound_itr = inbound.begin();
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
                unsigned char nak_code = *inbound_itr++;

                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
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
                unsigned char nak_code = *inbound_itr++;

                switch( nak_code )
                {
                    //  return Klondike error codes and let the CCU do the translation
                    case NAK_DirectTransmission_BusDisabled:            _error = Error_BusDisabled;           break;
                    case NAK_DirectTransmission_DTranBusy:              _error = Error_DTranBusy;             break;
                    case NAK_DirectTransmission_InvalidBus:             _error = Error_InvalidBus;            break;
                    case NAK_DirectTransmission_InvalidDLCType:         _error = Error_InvalidDLCType;        break;
                    case NAK_DirectTransmission_InvalidMessageLength:   _error = Error_InvalidMessageLength;  break;
                    case NAK_DirectTransmission_NoRoutes:               _error = Error_NoRoutes;              break;

                    case NAK_DirectTransmission_InvalidSequence:
                    {
                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        //  could do some trickery here and reset the command so it goes out again...

                        _error = Error_InvalidSequence;

                        break;
                    }

                    default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_DirectMessageRequest : unhandled NAK code (" << nak_code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

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

                sequence  = *inbound_itr++;
                sequence |= *inbound_itr++ << 8;

                snr       = *inbound_itr++;
                snr      |= *inbound_itr++ << 8;

                message_length = *inbound_itr++;

                _dtran_result.assign(inbound_itr, inbound_itr + message_length);
            }

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            if( response_command == CommandCode_NAK )
            {
                switch( *inbound_itr++ )
                {
                    case NAK_LoadBuffer_QueueEntries:
                    {
                        //  capture the expected sequence
                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        unsigned char number_rejected = *inbound_itr++;

                        while( number_rejected-- > 0 )
                        {
                            unsigned short rejected_sequence;

                            rejected_sequence  = *inbound_itr++;
                            rejected_sequence |= *inbound_itr++ << 8;

                            unsigned char rejected_nak_code = *inbound_itr++;

                            pending_work_t::iterator pending_itr = _pending_requests.find(rejected_sequence);

                            if( pending_itr == _pending_requests.end() )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_WaitingQueueWrite : could not find rejected queue entry (" << rejected_nak_code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            else
                            {
                                queue_entry_t &rejected_entry = *(pending_itr->second);

                                if( rejected_entry.resubmissions++ > QueueEntryResubmissionsMaximum
                                    || (rejected_nak_code != NAK_LoadBuffer_QueueFull &&
                                        rejected_nak_code != NAK_LoadBuffer_InvalidSequence) )
                                {
                                    if( rejected_entry.om )
                                    {
                                        Errors error = Error_Unknown;

                                        switch( rejected_nak_code )
                                        {
                                            case NAK_LoadBuffer_BusDisabled:           error = Error_BusDisabled;           break;
                                            case NAK_LoadBuffer_InvalidBus:            error = Error_InvalidBus;            break;
                                            case NAK_LoadBuffer_InvalidDLCType:        error = Error_InvalidDLCType;        break;
                                            case NAK_LoadBuffer_InvalidMessageLength:  error = Error_InvalidMessageLength;  break;
                                            case NAK_LoadBuffer_NoRoutes:              error = Error_NoRoutes;              break;
                                            default:
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_WaitingQueueWrite : unhandled NAK code (" << rejected_nak_code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                            }
                                        }

                                        _plc_results.push(queue_result_t(rejected_entry.om, error, ::time(0), byte_buffer_t()));
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_WaitingQueueWrite : system queue entry rejected (" << rejected_nak_code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    _waiting_requests.erase(pending_itr->second);
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_WaitingQueueWrite : CommandCode_ACK_NoData **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                pending_work_t::iterator pending_itr = _pending_requests.begin(),
                                         pending_end = _pending_requests.end();

                unsigned accepted               = *inbound_itr++;
                _device_queue_entries_available = *inbound_itr++;

                while( (pending_itr != pending_end) && accepted )
                {
                    _remote_requests.insert(make_pair(pending_itr->first, *(pending_itr->second)));
                    _waiting_requests.erase(pending_itr->second);
                    _device_queue_sequence++;
                    accepted--;
                    pending_itr++;
                }

                if( accepted )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_WaitingQueueWrite : accept > _pending_requests.size() in Cti::Protocol::Klondike::decode() (" << accepted << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                unsigned char nak_code = *inbound_itr++;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_ReplyQueueRead : CommandCode_NAK (" << nak_code << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _error = Error_Unknown;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : CommandCode_ReplyQueueRead : CommandCode_ACK_NoData **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                _read_toggle = *inbound_itr++ ^ 0x01;  //  we'll echo this bit back to the CCU when we read or ack next

                unsigned queue_entries_read = *inbound_itr++;

                for( int entry = 0; entry < queue_entries_read; entry++ )
                {
                    //  queue_response_t's constructor increments the inbound iterator
                    queue_response_t q(inbound_itr);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - read queue response (seq, timestamp, signal strength, result, message) "
                             << "(" << setfill('0') << hex
                                    << setw(4) << q.sequence                  << ", "
                                    << CtiTime(q.timestamp)                   << ", "
                                    << setw(2) << (unsigned)q.signal_strength << ", "
                                    << setw(2) << (unsigned)q.result          << ", ";

                        byte_buffer_t::iterator message_itr = q.message.begin();

                        while( message_itr != q.message.end() )
                        {
                            dout << hex << setw(2) << (unsigned)*message_itr++;
                        }

                        dout << dec;

                        dout <<  ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    remote_work_t::iterator remote_itr = _remote_requests.find(q.sequence);

                    if( remote_itr == _remote_requests.end() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unknown sequence (" << hex << setw(4) <<  q.sequence << ") received **** " << dec << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    else
                    {
                        if( remote_itr->second.om )
                        {
                            _plc_results.push(queue_result_t(remote_itr->second.om,
                                                             Error_None,  //  we'll eventually need to plug an error code in here,
                                                             q.timestamp, //    but right now, q.result is poorly defined
                                                             q.message));
                        }

                        _remote_requests.erase(remote_itr);
                    }
                }
            }

            _reading_device_queue = false;

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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled command code (" << _current_command.command_code << ") in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }
}



bool Klondike::isTransactionComplete( void ) const
{
    return _current_command.command == Command_Invalid ||
           _io_state                == IO_Complete     ||
           errorCondition();
}


bool Klondike::errorCondition( void ) const
{
    return _io_state == IO_Failed;

}



Klondike::Errors Klondike::errorCode() const
{
    return _error;
}



bool Klondike::addQueuedWork(const OUTMESS *om, const byte_buffer_t &payload, unsigned priority, unsigned char dlc_parms, unsigned char stages)
{
    sync_guard_t guard(_sync);

    _waiting_requests.insert(queue_entry_t(payload, priority, dlc_parms, stages, om));

    return true;
}

bool Klondike::hasQueuedWork() const
{
    return hasWaitingWork() || hasRemoteWork();
}


unsigned Klondike::queuedWorkCount() const
{
    sync_guard_t guard(_sync);

    return _waiting_requests.size() + _remote_requests.size();
}


bool Klondike::isLoadingDeviceQueue() const
{
    sync_guard_t guard(_sync);

    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _loading_device_queue;
}


bool Klondike::setLoadingDeviceQueue(bool loading)
{
    sync_guard_t guard(_sync);

    return (_loading_device_queue = loading);
}


bool Klondike::isReadingDeviceQueue() const
{
    sync_guard_t guard(_sync);

    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _reading_device_queue;
}


bool Klondike::setReadingDeviceQueue(bool reading)
{
    sync_guard_t guard(_sync);

    return (_reading_device_queue = reading);
}


struct report_waiting_requests
{
    report_waiting_requests(ostringstream &stream) : _stream(stream)  { };

    ostringstream &_stream;

    template<class T>
    operator()(const T &waiting)
    {
        if( waiting.om )
        {
            _stream << setw(8) << waiting.om->DeviceID << "|"
                    << setw(3) << waiting.priority     << "|"
                               << waiting.om->Request.CommandStr << endl;
        }
        else
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            _stream << setw(8) << -1               << "|"
                    << setw(3) << waiting.priority << "|"
                               << "(MCT-400 broadcast timesync)" << endl;
        }
    }
};


struct report_pending_requests
{
    report_pending_requests(ostringstream &stream) : _stream(stream)  { };

    ostringstream &_stream;

    template<class T>
    operator()(const T &pending)
    {
        if( pending.second->om )
        {
            _stream << setw(8) << pending.first                << "|"
                    << setw(8) << pending.second->om->DeviceID << "|"
                    << setw(3) << pending.second->priority     << "|"
                               << pending.second->om->Request.CommandStr << endl;
        }
        else
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            _stream << setw(8) << pending.first            << "|"
                    << setw(8) << -1                       << "|"
                    << setw(3) << pending.second->priority << "|"
                               << "(MCT-400 broadcast timesync)" << endl;
        }
    }
};


struct report_remote_requests
{
    report_remote_requests(ostringstream &stream) : _stream(stream)  { };

    ostringstream &_stream;

    template<class T>
    operator()(const T &remote)
    {
        if( remote.second.om )
        {
            _stream << setw(8) << remote.first               << "|"
                    << setw(8) << remote.second.om->DeviceID << "|"
                    << setw(3) << remote.second.priority     << "|"
                               << remote.second.om->Request.CommandStr << endl;
        }
        else
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            _stream << setw(8) << remote.first           << "|"
                    << setw(8) << -1                     << "|"
                    << setw(3) << remote.second.priority << "|"
                               << "(MCT-400 broadcast timesync)" << endl;
        }
    }
};


string Klondike::queueReport() const
{
    ostringstream report;

    sync_guard_t guard(_sync);

    report.fill(' ');

    report << "Waiting requests (INUSE) : " << setw(5) << _waiting_requests.size() << " : in Yukon's queue" << endl;

    {
        report << setw(8) << "MCT ID" << "|"
               << setw(3) << "Pri"    << "|"
                          << "Command" << endl;

        for_each(_waiting_requests.begin(),
                 _waiting_requests.end(),
                 report_waiting_requests(report));
    }

    report << "Pending requests (INUSE) : " << setw(5) << _pending_requests.size() << " : waiting for ACK from CCU" << endl;

    {
        report << setw(8) << "Queue ID" << "|"
               << setw(8) << "MCT ID"   << "|"
               << setw(3) << "Pri"      << "|"
                          << "Command" << endl;

        for_each(_pending_requests.begin(),
                 _pending_requests.end(),
                 report_pending_requests(report));
    }

    report << "Remote requests (INCCU)  : " << setw(5) << _remote_requests.size() << " : in the CCU's queue" << endl;

    {
        report << setw(8) << "Queue ID" << "|"
               << setw(8) << "MCT ID"   << "|"
               << setw(3) << "Pri"      << "|"
                          << "Command" << endl;

        for_each(_remote_requests.begin(),
                 _remote_requests.end(),
                 report_remote_requests(report));
    }

    return report.str();
}


bool Klondike::hasRemoteWork() const
{
    sync_guard_t guard(_sync);

    return !_remote_requests.empty() || _device_status.response_buffer_has_data;
}


struct store_max_priority
{
    unsigned max_priority;

    store_max_priority(unsigned base_priority) : max_priority(base_priority) { };

    template<class T>
    operator()(T element)
    {
        max_priority = max(max_priority, element.second.priority);
    }
};


unsigned Klondike::getRemoteWorkPriority() const
{
    sync_guard_t guard(_sync);

    store_max_priority store(QueueReadBasePriority);

    for_each(_remote_requests.begin(),
             _remote_requests.end(),
             store);

    return store.max_priority;
}


bool Klondike::hasWaitingWork() const
{
    sync_guard_t guard(_sync);

    return !_waiting_requests.empty();
}


unsigned Klondike::getWaitingWorkPriority() const
{
    sync_guard_t guard(_sync);

    unsigned priority = QueueWriteBasePriority;

    //  The first entry in the set will always have the highest priority
    if( !_waiting_requests.empty() )
    {
        priority = max(priority, _waiting_requests.begin()->priority);
    }

    return priority;
}


void Klondike::addRoute(const CtiRouteCCUSPtr &new_route)
{
    route_entry_t route;

    route.bus      = new_route->getBus();
    route.fixed    = new_route->getCCUFixBits();
    route.variable = new_route->getCCUVarBits();
    route.stages   = new_route->getStages();

    _routes.push_back(route);
}


void Klondike::clearRoutes()
{
    _routes.clear();
}


void Klondike::setWrap(Wrap *wrap)
{
    _wrap = wrap;
}

long Klondike::currentTime()
{
    return ::time(0);
}

}
}

