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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/10/31 20:51:56 $
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


void Klondike::setAddresses( unsigned short slaveAddress, unsigned short masterAddress )
{
    _masterAddress = masterAddress;
    _slaveAddress  = slaveAddress;

    _idlc_wrap.setAddress(_slaveAddress);
}


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


bool Klondike::setCommand( int command )
{
    switch( command )
    {
        case Command_DirectMessageRequest:  _command = Command_DirectMessageRequest;    break;

        case Command_WaitingQueueWrite:     _command = Command_WaitingQueueWrite;       break;
        case Command_WaitingQueueFreeze:    _command = Command_WaitingQueueFreeze;      break;
        case Command_WaitingQueueRead:      _command = Command_WaitingQueueRead;        break;
        case Command_WaitingQueueClear:     _command = Command_WaitingQueueClear;       break;
        case Command_ReplyQueueRead:        _command = Command_ReplyQueueRead;          break;

        case Command_TimeSyncCCU:           _command = Command_TimeSyncCCU;             break;
        case Command_TimeSyncTransmit:      _command = Command_TimeSyncTransmit;        break;

        case Command_ConfigurationMemoryRead:   _command = Command_ConfigurationMemoryRead;  break;
        case Command_ConfigurationMemoryWrite:  _command = Command_ConfigurationMemoryWrite; break;

        case Command_RoutingTableWrite:                 _command = Command_RoutingTableWrite;                    break;
        case Command_RoutingTableRead:                  _command = Command_RoutingTableRead;                     break;
        case Command_RoutingTableRequestAvailableSlots: _command = Command_RoutingTableRequestAvailableSlots;    break;
        case Command_RoutingTableClear:                 _command = Command_RoutingTableClear;                    break;

        default:
        {
            _command = Command_Invalid;
            break;
        }
    }

    _io_state = IO_Output;

    return _command != Command_Invalid;
}


void Klondike::setDirectTransmissionInfo( BSTRUCT &BSt )
{
    _dtran_bstruct = BSt;
}


int Klondike::generate( CtiXfer &xfer )
{
    if( _wrap->isTransactionComplete() )
    {
        switch( _io_state )
        {
            case IO_Output:     doOutput();     break;
            case IO_Input:      _wrap->recv();  break;
        }
    }

    return _wrap->generate(xfer);
}


void Klondike::doOutput()
{
    int length = 0;

    switch( _command )
    {
        case Command_DirectMessageRequest:
        {
            _outbound[length++] = _command;

            _outbound[length++] = (_sequence >> 8) & 0x00ff;
            _outbound[length++] =  _sequence       & 0x00ff;

            _outbound[length++] = _dtran_bstruct.DlcRoute.Bus;
            _outbound[length++] = _dtran_bstruct.DlcRoute.Stages;

            if( _dtran_bstruct.IO == Emetcon::IO_Read ||
                _dtran_bstruct.IO == Emetcon::IO_Function_Read )
            {
                B_Word(_outbound + length, _dtran_bstruct, Emetcon::determineDWordCount(_dtran_bstruct.Length));
                length += BWORDLEN;
            }
            else
            {
                B_Word(_outbound + length, _dtran_bstruct, (_dtran_bstruct.Length + 4) / 5);
                length += BWORDLEN;

                //  doesn't write anything if length <= 0
                C_Words(_outbound + length, _dtran_bstruct.Message, _dtran_bstruct.Length, 0);
                length += CWORDLEN * ((_dtran_bstruct.Length + 4) / 5);
            }

            _wrap->send(_outbound, length);

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


int Klondike::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;

    retVal = _wrap->decode(xfer, status);

    if( status )
    {
        _comm_errors++;
    }
    else if( _wrap->isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_DirectMessageRequest:
            {
                //  process DTRAN response ACK/NACK
                if( _io_state == IO_Output )
                {
                    _io_state = IO_Input;
                }
                else
                {
                    _command = Command_Complete;
                }

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

    if( _comm_errors > CommErrorMaximum )
    {
        _command = Command_Failed;
    }

    return retVal;
}


bool Klondike::isTransactionComplete( void ) const
{
    return _command == Command_Complete ||
           _command == Command_Invalid  ||
           _command == Command_Failed;
}


}
}

