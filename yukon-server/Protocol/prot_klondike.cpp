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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/06/13 13:39:49 $
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

namespace Cti       {
namespace Protocol  {

const Klondike::command_state_map_t Klondike::_command_states;


Klondike::Klondike() :
    _wrap(&_idlc_wrap),
    _io_state(IO_Invalid),
    _loading_device_queue(false),
    _reading_device_queue(false),
    _device_queue_sequence(0),
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
    (*this)[Command_Null];  //  initialize to an empty set

    (*this)[Command_Loopback ].push_back(CommandCode_CheckStatus);

    (*this)[Command_LoadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_LoadQueue].push_back(CommandCode_WaitingQueueWrite);

    (*this)[Command_ReadQueue].push_back(CommandCode_CheckStatus);
    (*this)[Command_ReadQueue].push_back(CommandCode_ReplyQueueRead);

    (*this)[Command_TimeSync ].push_back(CommandCode_TimeSyncCCU);
    (*this)[Command_TimeSync ].push_back(CommandCode_TimeSyncTransmit);

    (*this)[Command_DirectTransmission].push_back(CommandCode_DirectMessageRequest);

    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableClear);
    (*this)[Command_LoadRoutes].push_back(CommandCode_RoutingTableWrite);
}

/*
bool Klondike::checkCommandOverride()
{
    if( _remote_requests.empty() && _device_status.response_buffer_has_data )
    {
        _current_command.command = Command_ClearQueue;
    }
}
*/

bool Klondike::nextCommandState()
{
    bool complete = true;

    //checkCommandOverride();

    command_state_map_t::iterator itr = _command_states.find(_current_command.command);

    /*if( _current_command.command == Command_LoadQueue &&
        (_device_status.bits.transmit_buffer_full ||
         _device_status.bits.response_buffer_full) )
    {

    }
    else*/ if( itr != _command_states.end() && _current_command.state < itr->second.size() )
    {
        _current_command.command_code = itr->second[_current_command.state];
        _current_command.state++;

        complete = false;
    }
    else
    {
        _current_command.command_code = CommandCode_Null;
    }

    return !complete;
}


void Klondike::refreshMCTTimeSync(BSTRUCT &bst)
{
    if( bst.Function == CtiDeviceMCT4xx::FuncWrite_TSyncPos &&
        bst.Length   == CtiDeviceMCT4xx::FuncWrite_TSyncLen )
    {
        //  replicated from CtiDeviceMCT4xx::loadTimeSync()
        CtiTime now;
        unsigned long time = now.seconds();

        bst.Message[0] = gMCT400SeriesSPID;  //  global SPID
        bst.Message[1] = (time >> 24) & 0x000000ff;
        bst.Message[2] = (time >> 16) & 0x000000ff;
        bst.Message[3] = (time >>  8) & 0x000000ff;
        bst.Message[4] =  time        & 0x000000ff;
        bst.Message[5] = now.isDST();
    }
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

int Klondike::setCommand( const OUTMESS *const outmessage )
{
    int command;

    if( outmessage->EventCode & DTRAN &&
        outmessage->EventCode & BWORD )
    {
        command = Klondike::Command_DirectTransmission;
        _dtran_outmess = CTIDBG_new OUTMESS(*outmessage);
    }
    else
    {
        command = outmessage->Sequence;
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


int Klondike::generate( CtiXfer &xfer )
{
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

    unsigned char outbound[255];

    //  first byte is always the command code
    outbound[pos++] = command_code;

    switch( command_code )
    {
        //  no additional data
        case CommandCode_CheckStatus:
        case CommandCode_TimeSyncTransmit:
        {
            break;
        }

        case CommandCode_DirectMessageRequest:
        {
            outbound[pos++] =  _device_queue_sequence       & 0x00ff;
            outbound[pos++] = (_device_queue_sequence >> 8) & 0x00ff;

            outbound[pos++] = _dtran_outmess->Buffer.BSt.DlcRoute.Bus + 1;
            outbound[pos++] = _dtran_outmess->Buffer.BSt.DlcRoute.Stages;

            //  save the length position - it gets filled in after we write the Emetcon words
            length_pos = pos++;

            refreshMCTTimeSync(_dtran_outmess->Buffer.BSt);

            dlc_length = writeDLCMessage(outbound + pos, _dtran_outmess->Buffer.BSt);

            outbound[length_pos] = dlc_length;

            pos += dlc_length;

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            if( _waiting_requests.empty() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - _waiting_requests.empty() with command_code = CommandCode_WaitingQueueWrite in Cti::Protocol::Klondike::doOutput() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _io_state = IO_Complete;
            }
            else
            {
                outbound[pos++] =  _device_queue_sequence       & 0x00ff;
                outbound[pos++] = (_device_queue_sequence >> 8) & 0x00ff;

                int queue_count_pos = pos++;

                local_work_t::iterator itr,
                                       itr_begin = _waiting_requests.begin(),
                                       itr_end   = _waiting_requests.end();

                int processed = 0,
                    limit     = _wrap->getMaximumPayload();

                bool full = false;

                for( itr = itr_begin; itr != itr_end && !full && processed < _device_queue_entries_available; itr++ )
                {
                    if( itr )
                    {
                        const OUTMESS *om = *itr;

                        unsigned entry_len = QueueEntryHeaderLength + calcDLCMessageLength(om->Buffer.BSt);

                        if( pos + entry_len > limit )
                        {
                            full = true;
                        }
                        else
                        {
                            unsigned char dlc_parms;

                            dlc_parms = (om->Buffer.BSt.DlcRoute.Bus + 1);

                            outbound[pos++] = om->Priority;
                            outbound[pos++] = dlc_parms;
                            outbound[pos++] = om->Buffer.BSt.DlcRoute.Stages;

                            //  save the length position - it gets filled in after we write the Emetcon words
                            length_pos = pos++;

                            dlc_length = writeDLCMessage(outbound + pos, om->Buffer.BSt);

                            outbound[length_pos] = dlc_length;

                            pos += dlc_length;

                            _pending_requests.insert(std::make_pair(_device_queue_sequence, itr));

                            processed++;
                        }
                    }
                    else
                    {
                        //  null OM, just delete it
                        _waiting_requests.erase(itr);
                    }
                }

                outbound[queue_count_pos] = processed;
            }

            break;
        }

        case CommandCode_ReplyQueueRead:
        {
            if( _remote_requests.empty() && !_device_status.response_buffer_has_data )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - _remote_requests.empty() && !_device_status.response_buffer_has_data with command_code = CommandCode_ReplyQueueRead in Cti::Protocol::Klondike::doOutput() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _io_state = IO_Complete;
            }
            else
            {
                outbound[pos++] = _read_toggle;

            }

            break;
        }

        case CommandCode_RoutingTableClear:
        {
            outbound[pos++] = 0x00;  //  clear all slots

            break;
        }

        case CommandCode_RoutingTableWrite:
        {
            if( _routes.empty() )
            {
                _io_state = IO_Complete;  //  abort!
            }
            else
            {
                if( _routes.size() <= 32 )
                {
                    outbound[pos++] = _routes.size();
                }
                else
                {
                    outbound[pos++] = 32;
                }

                route_list_t::iterator itr = _routes.begin();
                int route = 1;

                while( itr != _routes.end() && route <= 32 )
                {
                    outbound[pos++] = route;
                    outbound[pos++] = (itr->variable) << 5 | itr->fixed;
                    outbound[pos++] = itr->stages;
                    outbound[pos++] = itr->bus;
                    outbound[pos++] = itr->spid;

                    route++;
                    itr++;
                }
            }

            break;
        }

        /*
        case CommandCode_WaitingQueueFreeze:
        case CommandCode_WaitingQueueClear:
        */

        case CommandCode_TimeSyncCCU:
        {
            unsigned long now = CtiTime::now().seconds();

            outbound[pos++] = now & 0xff;   now >>= 8;
            outbound[pos++] = now & 0xff;   now >>= 8;
            outbound[pos++] = now & 0xff;   now >>= 8;
            outbound[pos++] = now & 0xff;

            break;
        }

        /*
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

    //  IO_Complete is the "abort" setting
    if( _io_state != IO_Complete )
    {
        _wrap->send(outbound, pos);
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


unsigned Klondike::writeDLCMessage(unsigned char *buf, const BSTRUCT &bst )
{
    unsigned pos = 0;

    if( bst.IO == Emetcon::IO_Read ||
        bst.IO == Emetcon::IO_Function_Read )
    {
        B_Word(buf + pos, bst, Emetcon::determineDWordCount(bst.Length));
        pos += BWORDLEN;
    }
    else
    {
        int words = (bst.Length + 4) / 5;

        B_Word(buf + pos, bst, words);
        pos += BWORDLEN;

        //  doesn't write anything if length <= 0
        C_Words(buf + pos, (unsigned char *)bst.Message, bst.Length, 0);
        pos += CWORDLEN * words;
    }

    return pos;
}


unsigned Klondike::calcDLCMessageLength( const BSTRUCT &bst )
{
    unsigned length = BWORDLEN;

    if( bst.IO == Emetcon::IO_Write ||
        bst.IO == Emetcon::IO_Function_Write )
    {
        length += ((bst.Length + 4) / 5) * CWORDLEN;
    }

    return length;
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
    int retVal = NoError;

    retVal = _wrap->decode(xfer, status);

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
                    unsigned char inbound[WrapLengthMaximum];
                    unsigned length;

                    //  process DTRAN response ACK/NACK
                    if( (length = _wrap->getInboundDataLength()) > WrapLengthMaximum )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - _wrap->getInboundDataLength() > WrapLengthMaximum ("
                                          << _wrap->getInboundDataLength() << " > " << WrapLengthMaximum
                                          << ") in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    else
                    {
                        _wrap->getInboundData(inbound);

                        processResponse(inbound, length);
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

    return retVal;
}


void Klondike::processResponse(unsigned char *inbound, unsigned char inbound_length)
{
    unsigned char pos = 0;
    unsigned char response_command  = inbound[pos++];
    unsigned char requested_command = inbound[pos++];

    _device_status.as_bytes[0] = inbound[pos++];
    _device_status.as_bytes[1] = inbound[pos++];

    switch( _current_command.command_code )
    {
        case CommandCode_CheckStatus:
        {
            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = inbound[pos++];

                _error_code = NOTNORMAL;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error_code = NOTNORMAL;
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                _device_queue_entries_available = inbound[pos++];
                _device_queue_sequence  = inbound[pos++];
                _device_queue_sequence |= (inbound[pos++] << 8);
            }
            else
            {
                _error_code = NOTNORMAL;
            }

            break;
        }

        case CommandCode_DirectMessageRequest:
        {
            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = inbound[pos++];

                switch( nak_code )
                {
                    case 0x00:
                    case 0x01:
                    {
                        break;
                    }
                    case 0x02:
                    {
                        _device_queue_sequence  =  inbound[pos++];
                        _device_queue_sequence |= (inbound[pos++] << 8);

                        //  could do some trickery here and reset the command so it goes out again...

                        break;
                    }
                }

                _error_code = NOTNORMAL;
            }
            else if( response_command == CommandCode_ACK_NoData )
            {
                _error_code = NOTNORMAL;
            }
            else
            {
                unsigned short sequence, snr;
                unsigned char  message_length;

                sequence  = inbound[pos++];
                sequence |= inbound[pos++] << 8;

                snr       = inbound[pos++];
                snr      |= inbound[pos++] << 8;

                message_length = inbound[pos++];

                if( inbound_length < message_length + pos )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - inbound length < message_length + 9 (" << inbound_length << " < " << message_length + 9 << ") in Cti::Protocol::Klondike::processResponse_DirectMessage() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                else if( message_length > DWORDLEN * 3 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - message_length > DWORDLEN * 3 (" << message_length << " > " << DWORDLEN * 3 << ") in Cti::Protocol::Klondike::processResponse_DirectMessage() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                else
                {
                    INMESS *im = CTIDBG_new INMESS;

                    OutEchoToIN(_dtran_outmess, im);

                    im->Port   = _dtran_outmess->Port;
                    im->Remote = _dtran_outmess->Remote;

                    im->Buffer.DSt.Address = _dtran_outmess->Buffer.BSt.Address;

                    memcpy(im->Buffer.InMessage, inbound + pos, message_length);

                    im->Time      = CtiTime::now().seconds();
                    im->InLength  = message_length;

                    _dlc_results.push(std::make_pair(_dtran_outmess, im));

                    _dtran_outmess = 0;

                    pos += message_length;
                }

                break;
            }

            break;
        }

        case CommandCode_WaitingQueueWrite:
        {
            if( response_command == CommandCode_NAK )
            {
                unsigned char nak_code = inbound[pos++];

                switch( nak_code )
                {
                    case 0x00:
                    {
                        //  capture the expected sequence
                        _device_queue_sequence  =  inbound[pos++];
                        _device_queue_sequence |= (inbound[pos++] << 8);

                        unsigned char  rejected = inbound[pos++],
                                       rejected_nak_code;
                        unsigned short rejected_sequence;

                        while( rejected > 0 )
                        {
                            rejected--;

                            rejected_sequence  =  inbound[pos++];
                            rejected_sequence |= (inbound[pos++] << 8);

                            pending_work_t::iterator itr = _pending_requests.find(rejected_sequence);

                            if( itr != _pending_requests.end() )
                            {
                                _pending_requests.erase(itr);
                            }

                            rejected_nak_code  = inbound[pos++];
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
                _loading_device_queue = false;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - ACK/No data in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                pending_work_t::iterator itr     = _pending_requests.begin(),
                                         itr_end = _pending_requests.end();

                unsigned accepted               = inbound[pos++];
                _device_queue_entries_available = inbound[pos++];

                while( itr != itr_end && accepted )
                {
                    _remote_requests.insert(std::make_pair(itr->first, *(itr->second)));
                    _waiting_requests.erase(itr->second);
                    _device_queue_sequence++;
                    accepted--;
                    itr++;
                }

                if( accepted )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - accept > _pending_requests.size() in Cti::Protocol::Klondike::decode() (" << accepted << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _pending_requests.clear();

                _loading_device_queue = false;

                //  should we try to load another back-to-back eventually?  what's the limit?  two full-boat messages?
            }

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
                _reading_device_queue = false;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - ACK/No data in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( response_command == CommandCode_ACK_Data )
            {
                _read_toggle = inbound[pos++] ^ 0x01;  //  we'll echo this bit back to the CCU when we read or ack next

                unsigned queue_entries_read = inbound[pos++];

#pragma pack( push, 1 )
                struct queue_response
                {
                    unsigned short sequence;
                    unsigned long  timestamp;
                    unsigned short signal_strength;
                    unsigned char  result;
                    unsigned char  message_length;
                    unsigned char  message[DWORDLEN * 3];  //  3 D words is the maximum that we can bring back
                } *q;
#pragma pack( pop )

                for( int entry = 0; entry < queue_entries_read; entry++ )
                {
                    q = reinterpret_cast<queue_response *>(inbound + pos);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - read queue response (seq, timestamp, signal strength, result, message) "
                             << "(" << CtiNumStr(q->sequence).xhex(4)        << ", "
                                    << CtiTime(q->timestamp)                 << ", "
                                    << CtiNumStr(q->signal_strength).xhex(2) << ", "
                                    << CtiNumStr(q->result).xhex(2)          << ", ";

                        for( int i = 0; i < q->message_length; i++ )
                        {
                            dout << CtiNumStr(q->message[i]).hex(2);
                        }

                        dout <<  ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    remote_work_t::iterator itr = _remote_requests.find(q->sequence);

                    if( itr == _remote_requests.end() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unknown sequence (" << CtiNumStr(q->sequence).xhex(4) << ") received **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    else
                    {
                        const OUTMESS *om = itr->second;
                        INMESS *im = CTIDBG_new INMESS;

                        OutEchoToIN(om, im);

                        im->Port      = om->Port;
                        im->Remote    = om->Remote;

                        memcpy(im->Buffer.InMessage,  q->message, q->message_length);

                        im->Buffer.DSt.Address = om->Buffer.BSt.Address;

                        im->EventCode = q->result;
                        im->Time      = q->timestamp;
                        im->InLength  = q->message_length;

                        _dlc_results.push(std::make_pair(om, im));

                        _remote_requests.erase(itr);
                    }

                    //  increment past this queue entry
                    pos += sizeof(queue_response) - (DWORDLEN * 3) + q->message_length;
                }

                _reading_device_queue = false;
            }

            break;
        }

        /*
        case Command_WaitingQueueFreeze:
        case Command_WaitingQueueRead:
        case Command_WaitingQueueClear:

        case Command_TimeSyncCCU:
        case Command_TimeSyncTransmit:

        case Command_RoutingTableWrite:
        case Command_RoutingTableRead:
        case Command_RoutingTableRequestAvailableSlots:
        case Command_RoutingTableClear:

        case Command_ConfigurationMemoryRead:
        case Command_ConfigurationMemoryWrite:
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



bool Klondike::addQueuedWork(OUTMESS *&OutMessage)
{
    _waiting_requests.insert(OutMessage);

    OutMessage = 0;

    return true;
}

bool Klondike::hasQueuedWork() const
{
    return hasWaitingWork() || hasRemoteWork();
}


unsigned Klondike::queuedWorkCount() const
{
    return _waiting_requests.size() + _remote_requests.size();
}


bool Klondike::isLoadingDeviceQueue() const
{
    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _loading_device_queue;
}


bool Klondike::setLoadingDeviceQueue(bool loading)
{
    return (_loading_device_queue = loading);
}


bool Klondike::isReadingDeviceQueue() const
{
    //  TODO: add a timeout here for when the queue entry is lost.
    //    Note, however, this should only happen if the queues are purged, which will be next to never.
    return _reading_device_queue;
}


bool Klondike::setReadingDeviceQueue(bool reading)
{
    return (_reading_device_queue = reading);
}


bool Klondike::hasRemoteWork() const
{
    return !_remote_requests.empty() || _device_status.response_buffer_has_data;
}


unsigned Klondike::getRemoteWorkPriority() const
{
    unsigned priority = QueueReadBasePriority;

    remote_work_t::iterator itr,
                            itr_begin = _remote_requests.begin(),
                            itr_end = _remote_requests.end();

    //  iterate over all work in the CCU that needs to be retrieved
    for( itr = itr_begin; itr != itr_end; itr++ )
    {
        if( itr->second && itr->second->Priority > priority )
        {
            priority = itr->second->Priority;
        }
    }

    return priority;
}


bool Klondike::hasWaitingWork() const
{
    return !_waiting_requests.empty();
}


unsigned Klondike::getWaitingWorkPriority() const
{
    unsigned priority = QueueWriteBasePriority;

    //  _local_entries is a set of pointers, so we check to make sure the pointer isn't null before we dereference it
    //  Note that the first entry in the set will always have the highest priority, since they're sorted with om_sort
    if( !_waiting_requests.empty()
        && (*(_waiting_requests.begin()))
        && (*(_waiting_requests.begin()))->Priority > priority )
    {
        priority = (*(_waiting_requests.begin()))->Priority;
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

