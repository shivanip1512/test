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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/10/05 16:44:51 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "numstr.h"
#include "prot_klondike.h"

namespace Cti       {
namespace Protocol  {

Klondike::Klondike() :
    _command(Command_Invalid)
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

    //_app_layer.setAddresses(_slaveAddress, _masterAddress);
}


void Klondike::setWrap( Wrap w )
{
    _protocol_wrap = w;
}


bool Klondike::setCommand( Command command )
{
    _command = command;

    return _command != Command_Invalid;
}


int Klondike::wrap_generate( CtiXfer &xfer )
{
    int retval = NoMethod;

    switch( _protocol_wrap )
    {
        case Wrap_IDLC: retval = _idlc_wrap.generate(xfer);  break;
        case Wrap_DNP:  retval = _dnp_wrap.generate(xfer);   break;
    }

    return retval;
}


int Klondike::wrap_decode( CtiXfer &xfer, int status )
{
    int retval = NoMethod;

    switch( _protocol_wrap )
    {
        case Wrap_IDLC: retval = _idlc_wrap.decode(xfer, status);  break;
        case Wrap_DNP:  retval = _dnp_wrap.decode(xfer, status);   break;
    }

    return retval;
}


bool Klondike::wrap_isTransactionComplete( void )
{
    bool retval = true;

    switch( _protocol_wrap )
    {
        case Wrap_IDLC: retval = _idlc_wrap.isTransactionComplete();  break;
        case Wrap_DNP:  retval = _dnp_wrap.isTransactionComplete();   break;
    }

    return retval;
}


int Klondike::generate( CtiXfer &xfer )
{
    if( wrap_isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_DirectMessageRequest:

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

    return wrap_generate(xfer);
}


int Klondike::decode( CtiXfer &xfer, int status )
{
    int retVal;

    wrap_decode(xfer, status);

    if( status )
    {
        _comm_errors++;
    }
    else if( wrap_isTransactionComplete() )
    {
        switch( _command )
        {
            case Command_DirectMessageRequest:

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


bool Klondike::isTransactionComplete( void )
{
    return _command == Command_Complete ||
           _command == Command_Invalid  ||
           _command == Command_Failed;
}


}
}

