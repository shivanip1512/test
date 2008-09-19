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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/09/19 11:40:41 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_klondike.h"
#include "prot_emetcon.h"
#include "porter.h"
#include "dev_mct4xx.h"  //  for MCT-4xx timesyncs

using namespace std;

namespace Cti       {
namespace Protocol  {

const Klondike::command_state_map_t Klondike::_command_states;


Klondike::Klondike() :
    _wrap(&_idlc_wrap),
    _io_state(IO_Invalid),
    _loading_device_queue(false),
    _reading_device_queue(false),
    _device_queue_sequence(numeric_limits<unsigned int>::max()),
    _read_toggle(0x00)
{
    _device_status.as_ushort = 0;
    _current_command.command = Command_Null;
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

    (*this)[Command_Null];  //  initialize to an empty set

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

    (*this)[Command_Raw       ].push_back(CommandCode_CheckStatus);  //  will not be used, but I needed something valid
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
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : (Command_LoadQueue/CommandCode_CheckStatus) : _waiting_requests.empty() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            command_valid = false;
        }

        if( _current_command.command_code == CommandCode_WaitingQueueWrite
            && _device_queue_entries_available == 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : (Command_LoadQueue/CommandCode_CheckStatus) : _device_queue_entries_available == 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : (Command_ReadQueue/CommandCode_ReplyQueueRead) : _remote_requests.empty() && !_device_status.response_buffer_has_data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::commandStateValid() : (Command_LoadRoutes/CommandCode_RoutingTableWrite) : _routes.empty() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

/*
void Klondike::setWrap( ProtocolWrap w )
{
    switch( w )
    {
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid wrap protocol (" << w << ") specified in Cti::Protocol::Klondike::Klondike, defaulting to IDLC **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        case ProtocolWrap_IDLC:     _wrap = &_idlc_wrap;    break;
        //case ProtocolWrap_DNP:      _wrap = &_dnp_wrap;     break;
    }
}
*/

//  this is only called from recvCommRequest, so only one thread should be able to call this at once
int Klondike::setCommand( const OUTMESS *const outmessage )
{
    int command;

    sync_guard_t guard(_sync);

    if( outmessage->EventCode & DTRAN &&
        outmessage->EventCode & BWORD )
    {
        command = Klondike::Command_DirectTransmission;
        _dtran_outmess = CTIDBG_new OUTMESS(*outmessage);
    }
    else
    {
        command = outmessage->Sequence;

        if( command == Command_Raw )
        {
            _raw_command.assign(outmessage->Buffer.OutMessage, outmessage->Buffer.OutMessage + outmessage->OutLength);
        }
    }

    command_state_map_t::const_iterator itr = _command_states.find(static_cast<Command>(command));

    if( itr == _command_states.end() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid command (" << command << ") in Klondike::setCommand() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _current_command.command      = Command_Null;
        _current_command.command_code = CommandCode_Null;
    }
    else
    {
        _current_command.command = itr->first;
        _current_command.state   = 0;

        if( nextCommandState() )
        {
            _wrap->init();
        }
    }

    _io_state   = IO_Output;
    _error_code = NoError;
    _protocol_errors = 0;

    return (_current_command.command == Command_Null)?(NoMethod):(NoError);
}


Klondike::Command Klondike::getCommand( void ) const
{
    return _current_command.command;
}


void Klondike::getResults( Klondike::result_queue_t &results )
{
    while( !_dlc_results.empty() )
    {
        results.push(_dlc_results.front());

        _dlc_results.pop();
    }
}


string Klondike::describeCurrentStatus( void ) const
{
    ostringstream stream;

    sync_guard_t guard(_sync);

    stream << "_device_queue_entries_available = " << _device_queue_entries_available << "\n";
    stream << "_device_queue_sequence          = " << hex << setw(4) << _device_queue_sequence << endl;
    stream << "_device_status.as_ushort        = " << hex << setw(4) << _device_status.as_ushort        << "\n";

    if( _device_status.response_buffer_has_data        ) stream << "_device_status.response_buffer_has_data        " << endl;
    if( _device_status.response_buffer_has_marked_data ) stream << "_device_status.response_buffer_has_marked_data " << endl;
    if( _device_status.response_buffer_full            ) stream << "_device_status.response_buffer_full            " << endl;
    if( _device_status.transmit_buffer_has_data        ) stream << "_device_status.transmit_buffer_has_data        " << endl;
    if( _device_status.transmit_buffer_full            ) stream << "_device_status.transmit_buffer_full            " << endl;
    if( _device_status.transmit_buffer_frozen          ) stream << "_device_status.transmit_buffer_frozen          " << endl;
    if( _device_status.plc_transmitting_dtran_message  ) stream << "_device_status.plc_transmitting_dtran_message  " << endl;
    if( _device_status.plc_transmitting_buffer_message ) stream << "_device_status.plc_transmitting_buffer_message " << endl;
    if( _device_status.time_sync_required              ) stream << "_device_status.time_sync_required              " << endl;
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
            case IO_Output:     doOutput(_current_command.command_code);        break;
            case IO_Input:      doInput (_current_command.command_code, xfer);  break;
        }
    }

    return _wrap->generate(xfer);
}


void Klondike::doOutput(CommandCode command_code)
{
    int pos = 0, length_pos, dlc_length = 0;

    vector<unsigned char> outbound;

    if( _current_command.command == Command_Raw )
    {
        //  this block of code could be moved up to generate()... ?  but then
        copy(_raw_command.begin(), _raw_command.end(), outbound.end());
    }
    else
    {
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

                outbound.push_back(_dtran_outmess->Buffer.BSt.DlcRoute.Bus + 1);
                outbound.push_back(_dtran_outmess->Buffer.BSt.DlcRoute.Stages);

                writeDLCMessage(outbound, _dtran_outmess);

                break;
            }

            case CommandCode_WaitingQueueWrite:
            {
                outbound.push_back( _device_queue_sequence       & 0x00ff);
                outbound.push_back((_device_queue_sequence >> 8) & 0x00ff);

                int queue_count_pos = outbound.size();
                outbound.push_back(0);

                local_work_t::iterator itr     = _waiting_requests.begin(),
                                       itr_end = _waiting_requests.end();

                int processed = 0,
                    limit     = _wrap->getMaximumPayload();

                bool full = false;

                for( ; itr != itr_end && !full && processed < _device_queue_entries_available; itr++ )
                {
                    if( pos + QueueEntryHeaderLength + itr->dlc_message.size() > limit )
                    {
                        full = true;
                    }
                    else
                    {
                        unsigned char dlc_parms = (itr->om->Buffer.BSt.DlcRoute.Bus + 1);

                        outbound.push_back(itr->om->Priority);
                        outbound.push_back(dlc_parms);
                        outbound.push_back(itr->om->Buffer.BSt.DlcRoute.Stages);
                        outbound.push_back(itr->dlc_message.size());
                        copy(itr->dlc_message.begin(), itr->dlc_message.end(), outbound.end());

                        _pending_requests.insert(make_pair(_device_queue_sequence, itr));

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
                int route = 1;

                while( itr != _routes.end() && route <= 32 )
                {
                    outbound.push_back(route);
                    outbound.push_back(itr->fixed | (itr->variable) << 5);
                    outbound.push_back(itr->stages);
                    outbound.push_back(itr->bus);
                    outbound.push_back(itr->spid);

                    route++;
                    itr++;
                }

                break;
            }

            case CommandCode_TimeSyncCCU:
            {
                unsigned long now = CtiTime::now().seconds();

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
    }

    //  IO_Complete is the "abort" setting
    if( _io_state != IO_Complete )
    {
        _wrap->send(outbound);
    }
}


void Klondike::doInput(CommandCode command_code, CtiXfer &xfer)
{
    _wrap->recv();

    switch( command_code )
    {
        //  normal case is to leave the xfer untouched...
        default:
        {
            break;
        }

        //  ...  but in the case of DTRAN, we need to add on
        //    some additional timeout time for the PLC comms
        case CommandCode_DirectMessageRequest:
        {
            //  hack the timeout into the xfer before we pass it to _wrap->generate()
            int timeout = TIMEOUT;

            if( _dtran_outmess->Buffer.BSt.IO & Emetcon::IO_Read )
            {
                timeout += _dtran_outmess->Buffer.BSt.DlcRoute.Stages * ((_dtran_outmess->Buffer.BSt.Length + 6 / 5) + 1);
            }
            else
            {
                timeout += _dtran_outmess->Buffer.BSt.DlcRoute.Stages * ((_dtran_outmess->Buffer.BSt.Length + 4 / 5) + 1);
            }

            xfer.setInTimeout(timeout);

            break;
        }
    }
}


unsigned Klondike::writeDLCMessage( vector<unsigned char> &buf, const OUTMESS *om )
{
    if( !om )   return 0;

    unsigned char length = 0, word_pos = 0;

    switch( om->EventCode & (AWORD | BWORD) )
    {
        case AWORD:
        {
            length = AWORDLEN;
            buf.push_back(length);
            word_pos = buf.size();
            buf.insert(buf.end(), length, 0);

            A_Word(buf.begin() + word_pos, om->Buffer.ASt);

            break;
        }
        case BWORD:
        {
            const BSTRUCT &bst = om->Buffer.BSt;

            if( bst.IO == Emetcon::IO_Read ||
                bst.IO == Emetcon::IO_Function_Read )
            {
                length = BWORDLEN;
                buf.push_back(length);
                word_pos = buf.size();
                buf.insert(buf.end(), length, 0);

                B_Word(buf.begin() + word_pos, bst, Emetcon::determineDWordCount(bst.Length));
            }
            else
            {
                int words = (bst.Length + 4) / 5;

                length = BWORDLEN;
                buf.push_back(length);
                word_pos = buf.size();
                buf.insert(buf.end(), length, 0);

                B_Word(buf.begin() + word_pos, bst, words);

                if( words )
                {
                    length = CWORDLEN * words;
                    buf.push_back(length);
                    word_pos = buf.size();
                    buf.insert(buf.end(), length, 0);

                    C_Words(buf.begin() + word_pos, (unsigned char *)bst.Message, bst.Length, 0);
                }
            }

            break;
        }
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::writeDLCMessage() : unhandled word type (" << (om->EventCode & (AWORD | BWORD)) << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return length;  //  not really used... ?
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

    _error_code = _wrap->decode(xfer, status);

    if( _wrap->isTransactionComplete() )
    {
        if( _wrap->errorCondition() )
        {
            if( ++_protocol_errors > ProtocolErrorsMaximum )
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
                    vector<unsigned char> inbound;

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

    return _error_code;
}


void Klondike::processResponse(const vector<unsigned char> &inbound)
{
    vector<unsigned char>::const_iterator inbound_itr = inbound.begin();
    unsigned char response_command  = *inbound_itr++;
    unsigned char requested_command = *inbound_itr++;

    _device_status.as_bytes[0] = *inbound_itr++;
    _device_status.as_bytes[1] = *inbound_itr++;

    switch( _current_command.command_code )
    {
        case CommandCode_TimeSyncCCU:
        {
            _waiting_requests.push_back()  //  add a timesync to the queue
        }
        case CommandCode_RoutingTableWrite:
        case CommandCode_RoutingTableClear:
        {
            if( response_command != CommandCode_ACK_NoData )
            {
                _error_code = NOTNORMAL;
            }

            break;
        }

        case CommandCode_CheckStatus:
        {
            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = *inbound_itr++;

                _error_code = NOTNORMAL;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error_code = NOTNORMAL;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                _device_queue_entries_available = *inbound_itr++;
                _device_queue_sequence  = *inbound_itr++;
                _device_queue_sequence |= *inbound_itr++ << 8;
            }
            else
            {
                _error_code = NOTNORMAL;
            }

            break;
        }

        case CommandCode_DirectMessageRequest:
        {
            INMESS *im = CTIDBG_new INMESS;

            OutEchoToIN(_dtran_outmess, im);

            im->Port   = _dtran_outmess->Port;
            im->Remote = _dtran_outmess->Remote;

            im->Time   = CtiTime::now().seconds();

            im->Buffer.DSt.Address = _dtran_outmess->Buffer.BSt.Address;

            _device_queue_sequence++;

            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = *inbound_itr++;

                switch( nak_code )
                {
                    case 0x00:
                    {
                        _error_code = BADROUTE;

                        break;
                    }
                    case 0x01:
                    {
                        _error_code = RTNF;

                        break;
                    }
                    case 0x02:
                    {
                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        //  could do some trickery here and reset the command so it goes out again...

                        //  this isn't quite accurate - we've been interpreting this as an IDLC error code for years, can we co-opt it?
                        _error_code = BADSEQUENCE;

                        break;
                    }
                    case 0x03:
                    case 0x04:
                    {
                        _error_code = BADBUSS;

                        break;
                    }
                }
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error_code = NOTNORMAL;
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

                if( distance(inbound_itr, inbound.end()) < message_length )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : distance(inbound_itr, inbound.end()) < message_length (" << distance(inbound_itr, inbound.end()) << " < " << message_length << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                    _error_code = NOTNORMAL;
                }
                else if( message_length > DWORDLEN * 3 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::processResponse() : message_length > DWORDLEN * 3 (" << message_length << " > " << DWORDLEN * 3 << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                    _error_code = NOTNORMAL;
                }
                else
                {
                    copy(inbound_itr, inbound_itr + message_length, im->Buffer.InMessage);

                    im->InLength  = message_length;
                }
            }

            im->EventCode = _error_code;

            _dlc_results.push(make_pair(_dtran_outmess, im));

            _dtran_outmess = 0;

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = *inbound_itr++;

                switch( nak_code )
                {
                    case 0x00:
                    {
                        //  capture the expected sequence
                        _device_queue_sequence  = *inbound_itr++;
                        _device_queue_sequence |= *inbound_itr++ << 8;

                        unsigned char  rejected = *inbound_itr++,
                                       rejected_nak_code;
                        unsigned short rejected_sequence;

                        while( rejected > 0 )
                        {
                            rejected--;

                            rejected_sequence  = *inbound_itr++;
                            rejected_sequence |= *inbound_itr++ << 8;

                            pending_work_t::iterator pending_itr = _pending_requests.find(rejected_sequence),
                                                     pending_end = _pending_requests.end();

                            if( pending_itr != pending_end )
                            {
                                _pending_requests.erase(pending_itr);
                            }

                            rejected_nak_code  = *inbound_itr++;
                        }
                    }
                    case 0x01:
                    case 0x02:
                    {
                        break;
                    }
                }
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - ACK/No data in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                pending_work_t::iterator pending_itr = _pending_requests.begin(),
                                         pending_end = _pending_requests.end();

                unsigned accepted               = *inbound_itr++;
                _device_queue_entries_available = *inbound_itr++;

                while( (pending_itr != pending_end) && accepted )
                {
                    _remote_requests.insert(make_pair(pending_itr->first, pending_itr->second->om));
                    _waiting_requests.erase(pending_itr->second);
                    _device_queue_sequence++;
                    accepted--;
                    pending_itr++;
                }

                if( accepted )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - accept > _pending_requests.size() in Cti::Protocol::Klondike::decode() (" << accepted << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                //  error handling - we got nacked
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - ACK/No data in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                _read_toggle = *inbound_itr++ ^ 0x01;  //  we'll echo this bit back to the CCU when we read or ack next

                unsigned queue_entries_read = *inbound_itr++;

                struct queue_response
                {
                    enum { header_size = 9 };
                    unsigned short sequence;
                    unsigned long  timestamp;
                    unsigned short signal_strength;
                    unsigned char  result;
                    vector<unsigned char> message;

                    queue_response(const unsigned char *&buf)
                    {
                        sequence         = *buf++;
                        sequence        |= *buf++ <<  8;

                        timestamp        = *buf++;
                        timestamp       |= *buf++ <<  8;
                        timestamp       |= *buf++ << 16;
                        timestamp       |= *buf++ << 32;

                        signal_strength  = *buf++;
                        signal_strength |= *buf++ <<  8;

                        result           = *buf++;

                        unsigned char message_length = *buf++;

                        message.insert(message.end(), buf, buf + message_length);
                        buf += message_length;
                    }
                };

                for( int entry = 0; entry < queue_entries_read; entry++ )
                {
                    queue_response q(inbound_itr);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - read queue response (seq, timestamp, signal strength, result, message) "
                             << "(" << setfill('0') << hex
                                    << setw(4) << q.sequence                  << ", "
                                    << CtiTime(q.timestamp)                        << ", "
                                    << setw(2) << (unsigned)q.signal_strength << ", "
                                    << setw(2) << (unsigned)q.result          << ", ";

                        vector<unsigned char>::iterator message_itr = q.message.begin();

                        while( message_itr != q.message.end() )
                        {
                            dout << hex << setw(2) << (unsigned)*message_itr;
                        }

                        dout <<  ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    remote_work_t::iterator remote_itr = _remote_requests.find(q.sequence);

                    if( remote_itr == _remote_requests.end() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unknown sequence (" << hex << setw(4) <<  q.sequence << ") received **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    else
                    {
                        const OUTMESS *om = remote_itr->second;
                        INMESS *im = CTIDBG_new INMESS;

                        OutEchoToIN(om, im);

                        im->Port      = om->Port;
                        im->Remote    = om->Remote;

                        copy(q.message.begin(), q.message.end(), im->Buffer.InMessage);

                        im->Buffer.DSt.Address = om->Buffer.BSt.Address;

                        im->EventCode = q.result;
                        im->Time      = q.timestamp;
                        im->InLength  = q.message.size();

                        _dlc_results.push(make_pair(om, im));

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
    return _current_command.command      == Command_Null     ||
           _current_command.command_code == CommandCode_Null ||
           _io_state == IO_Complete ||
           errorCondition();
}


bool Klondike::errorCondition( void ) const
{
    return _io_state == IO_Failed;
}



int Klondike::errorCode()
{
    return _error_code;
}



bool Klondike::addQueuedWork(OUTMESS *&OutMessage, bool broadcast)
{
    sync_guard_t guard(_sync);

    if( !OutMessage )  return false;

    queue_entry_t queue_entry(OutMessage);
    OutMessage = 0;  //  we control the horizontal and the vertical

    unsigned char dlc_parms = (OutMessage->Buffer.BSt.DlcRoute.Bus + 1);

    queue_entry.dlc_message.push_back(OutMessage->Priority);
    queue_entry.dlc_message.push_back(dlc_parms);
    queue_entry.dlc_message.push_back(OutMessage->Buffer.BSt.DlcRoute.Stages);

    writeDLCMessage(queue_entry.dlc_message, OutMessage);

    _waiting_requests.insert(queue_entry);

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
        _stream << setw(8) << waiting.om->DeviceID << "|"
                           << waiting.om->Priority << "|"
                           << waiting.om->Request.CommandStr << endl;
    }
};


struct report_pending_requests
{
    report_pending_requests(ostringstream &stream) : _stream(stream)  { };

    ostringstream &_stream;

    template<class T>
    operator()(const T &pending)
    {
        _stream << setw(8) << pending.first                << "|"
                << setw(8) << pending.second->om->DeviceID << "|"
                           << pending.second->om->Priority << "|"
                           << pending.second->om->Request.CommandStr << endl;
    }
};


struct report_remote_requests
{
    report_remote_requests(ostringstream &stream) : _stream(stream)  { };

    ostringstream &_stream;

    template<class T>
    operator()(const T &remote)
    {
        _stream << setw(8) << remote.first            << "|"
                << setw(8) << remote.second->DeviceID << "|"
                           << remote.second->Priority << "|"
                           << remote.second->Request.CommandStr << endl;
    }
};


string Klondike::queueReport() const
{
    ostringstream report;

    sync_guard_t guard(_sync);

    report << "Waiting requests (INUSE)" << endl;

    {
        report << setw(8) << "MCT ID" << "|"
               << setw(2) << "Pri"    << "|"
                          << "Command" << endl;

        for_each(_waiting_requests.begin(),
                      _waiting_requests.end(),
                      report_waiting_requests(report));
    }

    report << "Pending requests (INUSE)" << endl;

    {
        report << setw(8) << "Queue ID" << "|"
               << setw(8) << "MCT ID"   << "|"
               << setw(2) << "Pri"      << "|"
                          << "Command" << endl;

        for_each(_pending_requests.begin(),
                      _pending_requests.end(),
                      report_pending_requests(report));
    }

    report << "Remote requests (INCCU)" << endl;

    {
        report << setw(8) << "Queue ID" << "|"
               << setw(8) << "MCT ID"   << "|"
               << setw(2) << "Pri"      << "|"
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
        if( element.second &&
            element.second->Priority > max_priority )
        {
            max_priority = element.second->Priority;
        }
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


int Klondike::decodeDWords(unsigned char *input, unsigned input_length, unsigned Remote, DSTRUCT *DSt)
{
    int status = NoError;
    unsigned short unused;

    for( int i = 0; i * DWORDLEN < input_length && !status; i++ )
    {
        switch( i )
        {
            case 0:  status = D1_Word (input,                &DSt->Message[0], &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm);  break;
            case 1:  status = D23_Word(input + DWORDLEN,     &DSt->Message[3], &DSt->TSync, &unused);  break;
            case 2:  status = D23_Word(input + DWORDLEN * 2, &DSt->Message[8], &unused, &unused);  break;
        }
    }

    return status;
}


void Klondike::addRoute(const CtiRouteCCUSPtr &new_route)
{
    route_entry_t route;

    route.bus      = new_route->getBus();
    route.fixed    = new_route->getCCUFixBits();
    route.variable = new_route->getCCUVarBits();
    route.stages   = new_route->getStages();
    route.spid     = 0xff;  //  to be changed to system SPID

    _routes.push_back(route);
}


void Klondike::clearRoutes()
{
    _routes.clear();
}


}
}

