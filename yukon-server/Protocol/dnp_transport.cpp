/*-----------------------------------------------------------------------------*
*
* File:   dnp_transport
*
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/03/10 21:16:23 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dllbase.h"
#include "logger.h"
#include "dnp_transport.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Transport::Transport()
{
    _ioState     = Uninitialized;
    _payload_out.data = 0;
    _payload_in.data  = 0;
}

Transport::Transport(const Transport &aRef)
{
    *this = aRef;
}

Transport::~Transport()
{
}

Transport &Transport::operator=(const Transport &aRef)
{
    if( this != &aRef )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *this;
}


void Transport::setAddresses(unsigned short dst, unsigned short src)
{
    _datalink.setAddresses(dst, src);
}


void Transport::setOptions(int options)
{
    _datalink.setOptions(options);
}


void Transport::resetLink( void )
{
    _datalink.resetLink();
}


int Transport::initForOutput(unsigned char *buf, int len, unsigned short dstAddr, unsigned short srcAddr)
{
    int retVal = NoError;

    _source_address      = srcAddr;
    _destination_address = dstAddr;

    if( len > 0 && buf )
    {
        _payload_out.data   = buf;
        _payload_out.length = len;
        _payload_out.sent   = 0;

        _sequence = 0;

        _ioState = Output;
    }
    else
    {
        _payload_out.data   = NULL;
        _payload_out.length = 0;
        _payload_out.sent   = 0;

        _ioState = Uninitialized;

        //  maybe set error return... ?

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - error initializing transport layer, len = \"" << len << "\", buf = \"" << buf << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return retVal;
}


int Transport::initForInput(unsigned char *buf)
{
    int retVal = NoError;

    _payload_in.data     = buf;
    _payload_in.length   = 0;
    _payload_in.received = 0;

    _ioState = Input;

    return retVal;
}


int Transport::generate( CtiXfer &xfer )
{
    int retval = NoError;
    int dataLen, packetLen, first, final;

    if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                //  prepare transport layer buf dude here man like and stuff for y'all

                first = !(_payload_out.sent > 0);

                _current_packet_length = _payload_out.length - _payload_out.sent;

                if( _current_packet_length > MaxPayloadLen )
                {
                    _current_packet_length = MaxPayloadLen;

                    final = 0;
                }
                else
                {
                    final = 1;
                }

                //  add on the header byte
                packetLen = _current_packet_length + HeaderLen;

                //  set up the transport header
                _out_packet.header.first = first;
                _out_packet.header.final = final;
                _out_packet.header.seq   = _sequence;

                //  copy the app layer chunk into the outbound packet
                memcpy( (void *)_out_packet.data, (void *)&(_payload_out.data[_payload_out.sent]), _current_packet_length );

                _datalink.setToOutput((unsigned char *)&_out_packet, packetLen);

                break;
            }

            case Input:
            {
                _datalink.setToInput();

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    retval = _datalink.generate(xfer);

    return retval;
}


int Transport::decode( CtiXfer &xfer, int status )
{
    int retVal = NoError;
    int datalinkStatus;

    datalinkStatus = _datalink.decode(xfer, status);

    if( _datalink.errorCondition() )
    {
        //  ACH: if( tries > maxtries ) or something
        _ioState = Failed;
        retVal   = datalinkStatus;

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                int transportPayloadLen;

                _sequence++;
                _payload_out.sent += _current_packet_length;

                if( _payload_out.length <= _payload_out.sent )
                {
                    _ioState = Complete;

                    if( _payload_out.length < _payload_out.sent )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - sent > length **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }

                break;
            }

            case Input:
            {
                int dataLen;

                //  copy out the data
                if( _datalink.getInPayloadLength() >= HeaderLen )
                {
                    dataLen = _datalink.getInPayloadLength() - HeaderLen;
                    _datalink.getInPayload((unsigned char *)&_in_packet);

                    memcpy(&_payload_in.data[_payload_in.received], _in_packet.data, dataLen);

                    _payload_in.received += dataLen;

                    //  ACH: verify incoming sequence numbers

                    if( _in_packet.header.final )
                    {
                        _ioState = Complete;
                    }
                }
                else
                {
                    _ioState = Failed;
                }

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _ioState = Failed;
            }
        }
    }

    return retVal;
}


bool Transport::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Failed || _ioState == Uninitialized;
}


bool Transport::errorCondition( void )
{
    return _ioState == Failed;
}


int Transport::getInputSize( void )
{
    return _payload_in.received;
}

}
}
}

