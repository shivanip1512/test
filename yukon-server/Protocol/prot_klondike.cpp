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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/01/21 20:54:00 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_klondike.h"
#include "prot_emetcon.h"

namespace Cti       {
namespace Protocol  {

const Klondike::command_mapping_t Klondike::_command_mapping;
const Klondike::error_mapping_t   Klondike::_error_mapping;

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


Klondike::error_mapping_t::error_mapping_t()
{
    using std::make_pair;
/*
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
*/
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

bool Klondike::setCommand( int command )
{
    command_mapping_t::const_iterator found_command = _command_mapping.find(command);

    if( found_command != _command_mapping.end() )
    {
        _command = found_command->second;
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
    _io_state    = IO_Output;

    return _command != Command_Invalid;
}


bool Klondike::setCommandDirectTransmission( const BSTRUCT &BSt )
{
    _dtran_bstruct = BSt;

    return setCommand(Command_DirectMessageRequest);
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

        }

        switch( _io_state )
        {
            case IO_Output:     doOutput();     break;
            case IO_Input:      _wrap->recv();  break;
        }

        if( _command  == Command_DirectMessageRequest &&
            _io_state == IO_Input )
        {
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
        }
    }

    return _wrap->generate(xfer);
}


void Klondike::doOutput()
{
    int length = 0, length_pos, dlc_length = 0;

    unsigned char outbound[255];

    switch( _command )
    {
        case Command_DirectMessageRequest:
        {
            outbound[length++] = _command;

            outbound[length++] = (_sequence >> 8) & 0x00ff;
            outbound[length++] =  _sequence       & 0x00ff;

            outbound[length++] = _dtran_bstruct.DlcRoute.Bus;
            outbound[length++] = _dtran_bstruct.DlcRoute.Stages;
            length_pos = length++;

            if( _dtran_bstruct.IO == Emetcon::IO_Read ||
                _dtran_bstruct.IO == Emetcon::IO_Function_Read )
            {
                B_Word(outbound + length, _dtran_bstruct, Emetcon::determineDWordCount(_dtran_bstruct.Length));
                length     += BWORDLEN;
                dlc_length += BWORDLEN;
            }
            else
            {
                B_Word(outbound + length, _dtran_bstruct, (_dtran_bstruct.Length + 4) / 5);
                length     += BWORDLEN;
                dlc_length += BWORDLEN;

                //  doesn't write anything if length <= 0
                C_Words(outbound + length, _dtran_bstruct.Message, _dtran_bstruct.Length, 0);
                length     += CWORDLEN * ((_dtran_bstruct.Length + 4) / 5);
                dlc_length += CWORDLEN * ((_dtran_bstruct.Length + 4) / 5);
            }

            outbound[length_pos] = dlc_length;

            _wrap->send(outbound, length);

            break;
        }

        /*
        case Command_WaitingQueueWrite:
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
        case Command_Complete:
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


bool Klondike::response_expected(Command command)
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

    if( _wrap->isTransactionComplete() )
    {
        if( _wrap->errorCondition() )
        {
            _protocol_errors++;
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
                    switch( _command )
                    {
                        case Command_DirectMessageRequest:
                        {
                            //  process DTRAN response ACK/NACK

                            unsigned char inbound[WrapLengthMaximum];
                            int length;

                            if( (length = _wrap->getInboundDataLength()) <= WrapLengthMaximum )
                            {
                                _wrap->getInboundData(inbound);

                                processResponse_DirectMessage(inbound, length);
                            }

                            _command = Command_Complete;
                            break;
                        }
                        /*
                        case Command_WaitingQueueWrite:
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
                        case Command_Complete:
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
            }
        }
    }

    if( _protocol_errors > ProtocolErrorsMaximum )
    {
        _command = Command_Failed;
    }

    return retVal;
}


void Klondike::processResponse_DirectMessage(unsigned char *inbound, unsigned char inbound_length)
{
    unsigned char pos = 0;
    unsigned char response_command  =  inbound[pos++];

    switch( response_command )
    {
        //  ACK/NACK enum defines
        case 0x80:
        {
            break;
        }

        case 0x81:
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

        case 0xc1:
        {
            unsigned char nak_code = inbound[pos++];

            switch( nak_code )
            {
                case NAK_DTran_DTranBusy:       //  break;
                case NAK_DTran_NoRoutes:        //  break;
                case NAK_DTran_InvalidSEQ:      //  break;
                case NAK_DTran_InvalidBUS:      //  break;
                case NAK_DTran_BUSDisabled:     //  break;
                case NAK_DTran_InvalidDLCType:  //  break;
                {
                    _error_code = nak_code;

                    break;
                }
                default:
                {
                    _error_code = NOTNORMAL;

                    break;
                }
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unknown response command (" << response_command << ") in Cti::Protocol::Klondike::processResponse_DirectMessage() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }
}



bool Klondike::isTransactionComplete( void ) const
{
    return _command == Command_Complete ||
           _command == Command_Invalid  ||
           _command == Command_Failed;
}


bool Klondike::errorCondition( void ) const
{
    return _command == Command_Failed;
}


int Klondike::errorCode()
{
    int error = NOTNORMAL;

    error_mapping_t::const_iterator found_error;// = _error_mapping.find(_error_code);

    if( found_error != _error_mapping.end() )
    {
        error = found_error->second;
    }

    return error;
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

