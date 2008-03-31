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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/03/31 21:17:35 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_klondike.h"
#include "prot_emetcon.h"
#include "dev_mct4xx.h"  //  for MCT-4xx timesyncs

namespace Cti       {
namespace Protocol  {

const Klondike::command_mapping_t Klondike::_command_mapping;
//const Klondike::error_mapping_t   Klondike::_error_mapping;

Klondike::Klondike() :
    _command(Command_Invalid),
    _sequence(0),
    _wrap(&_idlc_wrap),
    _io_state(IO_Invalid)
{
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


Klondike::command_mapping_t::command_mapping_t()
{
    using std::make_pair;

#define __insert_make_pair(X) insert(make_pair(X, X))

    __insert_make_pair(Command_CheckStatus);

    __insert_make_pair(Command_DirectMessageRequest);

    __insert_make_pair(Command_WaitingQueueWrite);
    __insert_make_pair(Command_WaitingQueueFreeze);
    __insert_make_pair(Command_WaitingQueueRead);
    __insert_make_pair(Command_WaitingQueueClear);
    __insert_make_pair(Command_ReplyQueueRead);

    __insert_make_pair(Command_TimeSyncCCU);
    __insert_make_pair(Command_TimeSyncTransmit);

    __insert_make_pair(Command_ConfigurationMemoryRead);
    __insert_make_pair(Command_ConfigurationMemoryWrite);

    __insert_make_pair(Command_RoutingTableWrite);
    __insert_make_pair(Command_RoutingTableRead);
    __insert_make_pair(Command_RoutingTableRequestAvailableSlots);
    __insert_make_pair(Command_RoutingTableClear);
}


/*
Klondike::error_mapping_t::error_mapping_t()
{
    using std::make_pair;

    insert(make_pair(Command_DirectMessageRequest,              Command_DirectMessageRequest));

    insert(make_pair(Command_WaitingQueueWrite,                 Command_WaitingQueueWrite));
    insert(make_pair(Command_WaitingQueueFreeze,                Command_WaitingQueueFreeze));
    insert(make_pair(Command_WaitingQueueRead,                  Command_WaitingQueueRead));
    insert(make_pair(Command_WaitingQueueClear,                 Command_WaitingQueueClear));
    insert(make_pair(Command_ReplyQueueRead,                    Command_ReplyQueueRead));

    insert(make_pair(Command_TimeSyncCCU,                       Command_TimeSyncCCU));
    insert(make_pair(Command_TimeSyncTransmit,                  Command_TimeSyncTransmit));

    insert(make_pair(Command_ConfigurationMemoryRead,           Command_ConfigurationMemoryRead));
    insert(make_pair(Command_ConfigurationMemoryWrite,          Command_ConfigurationMemoryWrite));

    insert(make_pair(Command_RoutingTableWrite,                 Command_RoutingTableWrite));
    insert(make_pair(Command_RoutingTableRead,                  Command_RoutingTableRead));
    insert(make_pair(Command_RoutingTableRequestAvailableSlots, Command_RoutingTableRequestAvailableSlots));
    insert(make_pair(Command_RoutingTableClear,                 Command_RoutingTableClear));
}
*/


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

bool Klondike::setCommand( int command )
{
    command_mapping_t::const_iterator found_command = _command_mapping.find(command);

    if( found_command != _command_mapping.end() )
    {
        _command = found_command->second;

        _wrap->init();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid command (" << command << ") in Klondike::setCommand() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        _command = Command_Invalid;
    }

    _protocol_errors = 0;
    _io_state        = IO_Output;
    _error_code      = NoError;

    return _command != Command_Invalid;
}


bool Klondike::setCommandDirectTransmission( const BSTRUCT &BSt )
{
    _dtran_bstruct = BSt;

    return setCommand(Command_DirectMessageRequest);
}


Klondike::Command Klondike::getCommand( void ) const
{
    return _command;
}


void Klondike::getResultDirectTransmission(unsigned char *buf, int buf_length, unsigned char &input_length)
{
    if( buf && (buf_length > _response_length) )
    {
        memcpy(buf, _d_words, _response_length);
        input_length = _response_length;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - !buf || buf_length <= response_length **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


int Klondike::generate( CtiXfer &xfer )
{
    if( _wrap->isTransactionComplete() )
    {
        if( _wrap->errorCondition() )
        {
            _io_state = IO_Failed;
        }
        else
        {
            switch( _io_state )
            {
                case IO_Output:
                {
                    doOutput();

                    break;
                }

                case IO_Input:
                {
                    doInput(xfer);

                    break;
                }
            }
        }
    }

    return _wrap->generate(xfer);
}


void Klondike::doOutput()
{
    int pos = 0, length_pos, dlc_length = 0;

    unsigned char outbound[255];

    //  first byte is always the command
    outbound[pos++] = _command;

    switch( _command )
    {
        case Command_CheckStatus:
        {
            break;
        }

        case Command_DirectMessageRequest:
        {
            outbound[pos++] = (_sequence >> 8) & 0x00ff;
            outbound[pos++] =  _sequence       & 0x00ff;

            outbound[pos++] = _dtran_bstruct.DlcRoute.Bus + 1;
            outbound[pos++] = _dtran_bstruct.DlcRoute.Stages;

            //  save the length position - it gets filled in after we write the Emetcon words
            length_pos = pos++;

            if( _dtran_bstruct.Function == CtiDeviceMCT4xx::FuncWrite_TSyncPos &&
                _dtran_bstruct.Length   == CtiDeviceMCT4xx::FuncWrite_TSyncLen )
            {
                //  replicated from CtiDeviceMCT4xx::loadTimeSync()
                CtiTime now;
                unsigned long time = now.seconds();

                _dtran_bstruct.Message[0] = gMCT400SeriesSPID;  //  global SPID
                _dtran_bstruct.Message[1] = (time >> 24) & 0x000000ff;
                _dtran_bstruct.Message[2] = (time >> 16) & 0x000000ff;
                _dtran_bstruct.Message[3] = (time >>  8) & 0x000000ff;
                _dtran_bstruct.Message[4] =  time        & 0x000000ff;
                _dtran_bstruct.Message[5] = now.isDST();
            }

            dlc_length = writeDLCMessage(outbound + pos, _dtran_bstruct);

            outbound[length_pos] = dlc_length;

            pos += dlc_length;

            _wrap->send(outbound, pos);

            break;
        }

        case Command_WaitingQueueWrite:
        {
            if( _waiting_requests.empty() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - _waiting_requests.empty() with _command = Command_WaitingQueueWrite in Cti::Protocol::Klondike::doOutput() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _io_state = IO_Complete;
            }
            else
            {
                outbound[pos++] = _command;

                outbound[pos++] = (_device_queue_sequence >> 8) & 0x00ff;
                outbound[pos++] =  _device_queue_sequence       & 0x00ff;

                outbound[pos++] = _device_queue_entries_available;

                local_work::iterator itr,
                                     itr_begin = _waiting_requests.begin(),
                                     itr_end   = _waiting_requests.end();

                int processed = 0,
                    limit     = _wrap->getMaximumPayload();

                bool complete = false;

                for( itr = itr_begin; itr != itr_end && !complete; itr++ )
                {
                    if( itr )
                    {
                        const OUTMESS *om = *itr;

                        unsigned entry_len = QueueEntryHeaderLength + calcDLCMessageLength(om->Buffer.BSt);

                        if( pos + entry_len < limit )
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
                        else
                        {
                            complete = true;
                        }
                    }
                    else
                    {
                        //  null OM, just delete it
                        _waiting_requests.erase(itr);
                    }

                    if( processed >= _device_queue_entries_available )
                    {
                        complete = true;
                    }
                }

                _wrap->send(outbound, pos);
            }

            break;
        }

        /*
        case Command_WaitingQueueFreeze:
        case Command_WaitingQueueRead:
        case Command_WaitingQueueClear:
        case Command_ReplyQueueRead:

        case Command_TimeSyncCCU:
        case Command_TimeSyncTransmit:

        case Command_RoutingTableWrite:
        case Command_RoutingTableRead:
        case Command_RoutingTableRequestAvailableSlots:
        case Command_RoutingTableClear:

        case Command_ConfigurationMemoryRead:
        case Command_ConfigurationMemoryWrite:
        */

        case Command_Invalid:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled command (" << _command << ") in Cti::Protocol::Klondike::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }
}


void Klondike::doInput(CtiXfer &xfer)
{
    _wrap->recv();

    switch( _command )
    {
        //  normal case is to leave the xfer untouched...
        default:
        {
            break;
        }

        //  ...  but in the case of DTRAN, we need to add on
        //    some additional timeout time for the PLC comms
        case Command_DirectMessageRequest:
        {
            //  hack the timeout into the xfer before we pass it to _wrap->generate()
            int timeout = TIMEOUT;

            if( _dtran_bstruct.IO & Emetcon::IO_Read )
            {
                timeout += _dtran_bstruct.DlcRoute.Stages * ((_dtran_bstruct.Length + 6 / 5) + 1);
            }
            else
            {
                timeout += _dtran_bstruct.DlcRoute.Stages * ((_dtran_bstruct.Length + 4 / 5) + 1);
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


bool Klondike::response_expected( Command command )
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
    return true;
}


int Klondike::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;

    retVal = _wrap->decode(xfer, status);

    if( !_wrap->isTransactionComplete() )
    {
        //  not ready for anything yet - maybe increment a sanity check?
    }
    else if( _wrap->errorCondition() && ++_protocol_errors > ProtocolErrorsMaximum )
    {
        _io_state = IO_Failed;
    }
    else
    {
        switch( _io_state )
        {
            case IO_Output:
            {
                if( response_expected(_command) )
                {
                    //  next time through generate(), set xfer to read the input
                    _io_state = IO_Input;
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

                    _io_state = IO_Complete;
                }
            }
        }
    }

    return retVal;
}


void Klondike::processResponse(unsigned char *inbound, unsigned char inbound_length)
{
    unsigned char pos = 0;
    unsigned char response_command  =  inbound[pos++];

    switch( _command )
    {
        case Command_CheckStatus:
        {
            inbound[0];

            break;
        }

        case Command_DirectMessageRequest:
        {
            if( response_command == Command_NAK )
            {
                unsigned char status1  = inbound[pos++],
                              status2  = inbound[pos++],
                              nak_code = inbound[pos++];

                _error_code = NOTNORMAL;
            }
            else if( response_command == Command_ACK_NoData )
            {
                _error_code = NOTNORMAL;
            }
            else
            {
                unsigned char requested_command =  inbound[pos++];
                unsigned char status_bytes[2]   = {inbound[pos++],
                                                   inbound[pos++]};
                unsigned short sequence, snr;
                unsigned char  message_length;

                sequence  = inbound[pos++] << 8;
                sequence |= inbound[pos++];

                snr       = inbound[pos++] << 8;
                snr      |= inbound[pos++];

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
                    memcpy(_d_words, inbound + pos, message_length);
                    _response_length = message_length;
                    pos += message_length;
                }

                break;
            }

            break;
        }

        case Command_WaitingQueueWrite:
        {
            if( inbound[0] == Command_NAK )
            {
                //  error handling - we got nacked
            }
            else if( inbound[0] == Command_ACK_NoData )
            {
                _loading_device_queue = false;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - ACK/No data in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( inbound[0] == Command_ACK_Data )
            {
                pending_work::iterator itr     = _pending_requests.begin(),
                                       itr_end = _pending_requests.end();

                unsigned accepted = inbound[1];
                _device_queue_entries_available = inbound[2];

                if( accepted != _pending_requests.size() )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - accepted != _pending_requests.size() in Cti::Protocol::Klondike::decode() (" << accepted << " != " << _pending_requests.size() <<") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                while( itr != itr_end )
                {
                    _remote_requests.insert(std::make_pair(itr->first, *(itr->second)));
                    _waiting_requests.erase(itr->second);
                }

                _pending_requests.clear();

                _loading_device_queue = false;

                //  should we try to load another back-to-back eventually?  what's the limit?  two full-boat messages?
            }

            break;
        }

        /*
        case Command_WaitingQueueFreeze:
        case Command_WaitingQueueRead:
        case Command_WaitingQueueClear:
        case Command_ReplyQueueRead:

        case Command_TimeSyncCCU:
        case Command_TimeSyncTransmit:

        case Command_RoutingTableWrite:
        case Command_RoutingTableRead:
        case Command_RoutingTableRequestAvailableSlots:
        case Command_RoutingTableClear:

        case Command_ConfigurationMemoryRead:
        case Command_ConfigurationMemoryWrite:
        */

        case Command_Invalid:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unhandled command (" << _command << ") in Cti::Protocol::Klondike::decode() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }
}



bool Klondike::isTransactionComplete( void ) const
{
    return _command == Command_Invalid
             || _io_state == IO_Complete
             || _io_state == IO_Failed;
}


bool Klondike::errorCondition( void ) const
{
    return _io_state == IO_Failed;
}


int Klondike::errorCode()
{
/*
    int error = NOTNORMAL;

    error_mapping_t::const_iterator found_error;// = _error_mapping.find(_error_code);

    if( found_error != _error_mapping.end() )
    {
        error = found_error->second;
    }

    return error;
*/
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
    return !_waiting_requests.empty() || !_remote_requests.empty();
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
    return !_remote_requests.empty();
}


unsigned Klondike::getRemoteWorkPriority() const
{
    unsigned priority = QueueWriteBasePriority;

    remote_work::iterator itr,
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


}
}

