#include "precompiled.h"

#include "dllbase.h"
#include "logger.h"
#include "dnp_transport.h"

using std::endl;
using std::vector;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

using Transport::TransportPacket;

TransportLayer::TransportLayer() :
    _current_payload_length(0),
    _sequence_in(0),
    _sequence_out(0),
    _source_address(0),
    _destination_address(0),
    _ioState(Uninitialized)
{
    memset( &_payload_in,  0, sizeof(payload_t) );
    memset( &_payload_out, 0, sizeof(payload_t) );
}

TransportLayer::TransportLayer(const TransportLayer &aRef)
{
    *this = aRef;
}

TransportLayer::~TransportLayer()
{
}

TransportLayer &TransportLayer::operator=(const TransportLayer &aRef)
{
    if( this != &aRef )
    {
        //TODO: Remove this log or make this class non-copyable
        CTILOG_TRACE(dout, "inside "<<__FUNCTION__);
    }

    return *this;
}


void TransportLayer::setAddresses(unsigned short dst, unsigned short src)
{
    _datalink.setAddresses(dst, src);
}


void TransportLayer::setOptions(int options)
{
    _datalink.setOptions(options);
}


void TransportLayer::resetLink( void )
{
    _datalink.resetLink();
}


int TransportLayer::initForOutput(unsigned char *buf, unsigned len, unsigned short dstAddr, unsigned short srcAddr)
{
    int retVal = ClientErrors::None;

    _source_address      = srcAddr;
    _destination_address = dstAddr;

    _payload_out.data   = buf;
    _payload_out.length = len;
    _payload_out.used = 0;

    _sequence_out = 0;

    if( len > 0 && buf )
    {
        _ioState = Output;
    }
    else
    {
        _payload_out.data   = NULL;
        _payload_out.length = 0;

        _ioState = Uninitialized;

        //  maybe set error return... ?

        CTILOG_ERROR(dout, "error initializing transport layer, len = \""<< len <<"\", buf = \""<< buf);
    }

    return retVal;
}


int TransportLayer::initForInput(unsigned char *buf, unsigned max_len)
{
    int retVal = ClientErrors::None;

    _payload_in.data   = buf;
    _payload_in.length = max_len;
    _payload_in.used = 0;

    _inbound_packets.clear();

    _ioState = Input;

    return retVal;
}


YukonError_t TransportLayer::generate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                TransportPacket out_packet(_payload_out.used == 0,
                                           _sequence_out,
                                           _payload_out.data + _payload_out.used,
                                           _payload_out.length - _payload_out.used);

                _current_payload_length = out_packet.payloadLength();

                vector<unsigned char> serialized = out_packet;

                //  do we need to observe a return value to handle any errors, or can we let it explode in generate()?
                _datalink.setToOutput(&serialized.front(), serialized.size());

                break;
            }

            case Input:
            {
                _datalink.setToInput();

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unhandled state ("<< _ioState <<")");
            }
            case Failed:
            {
                retVal = ClientErrors::Abnormal;
            }
        }
    }

    if( !retVal )
    {
        retVal = _datalink.generate(xfer);
    }

    return retVal;
}


YukonError_t TransportLayer::decode( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retVal = ClientErrors::None;

    if( retVal = _datalink.decode(xfer, status) )
    {
        //  make this more robust
        _ioState = Failed;
    }
    else if( _datalink.isTransactionComplete() )
    {
        switch( _ioState )
        {
            case Output:
            {
                _sequence_out = (_sequence_out + 1) & 0x3f;

                _payload_out.used += _current_payload_length;

                if( _payload_out.length <= _payload_out.used )
                {
                    _ioState = Complete;

                    if( _payload_out.length < _payload_out.used )
                    {
                        CTILOG_ERROR(dout, "payload sent > length ("<< _payload_out.used <<" > "<< _payload_out.length <<")");
                    }
                }

                break;
            }

            case Input:
            {
                unsigned dataLen;

                std::vector<unsigned char> inbound = _datalink.getInPayload();

                //  copy out the data
                if( inbound.size() >= TransportPacket::HeaderLen )
                {
                    TransportPacket packet(inbound.front(), ++inbound.begin(), inbound.end());

                    _inbound_packets.insert(packet);

                    if( isPacketSequenceValid(_inbound_packets) )
                    {
                        std::vector<unsigned char> payload = extractPayload(_inbound_packets);

                        if( payload.size() >= _payload_in.length )
                        {
                            CTILOG_ERROR(dout, "payload.size() >= _payload_in.length ("<< payload.size() <<" >= "<< _payload_in.length <<")");
                        }

                        _payload_in.used = std::min(_payload_in.length, payload.size());

                        memcpy(_payload_in.data, &payload.front(), _payload_in.used);

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
                CTILOG_ERROR(dout, "unhandled state ("<< _ioState <<")");

                _ioState = Failed;
            }
        }
    }

    return retVal;
}


bool TransportLayer::isPacketSequenceValid(const packet_sequence_t &packet_sequence)
{
    bool first_found = false;
    bool final_found = false;

    unsigned counter      = 0;
    unsigned wrap_counter = 0;

    for each( const TransportPacket &packet in packet_sequence )
    {
        //  make sure we only find one first and final packet
        if( (first_found && packet.isFirst()) ||
            (final_found && packet.isFinal()) )
        {
            return false;
        }

        //  if we found a final packet, the next one must be a first packet
        if( final_found && !first_found && !packet.isFirst() )
        {
            return false;
        }

        if( packet.isFirst() )
        {
            counter = packet.sequence();
        }

        first_found |= packet.isFirst();
        final_found |= packet.isFinal();

        if( first_found )
        {
            if( packet.sequence() != counter )
            {
                return false;
            }

            counter++;
        }
        else
        {
            if( packet.sequence() != wrap_counter )
            {
                return false;
            }

            wrap_counter++;
        }
    }

    return first_found &&
           final_found &&
           (wrap_counter ==  0||  //  normal case, wrap_counter untouched
            counter      == 64);  //  wraparound condition
}


std::vector<unsigned char> TransportLayer::extractPayload(const packet_sequence_t &packet_sequence)
{
    std::list<std::vector<unsigned char> > ordered_packets;

    std::list<std::vector<unsigned char> >::iterator insert_point = ordered_packets.end();

    for each( const TransportPacket &packet in packet_sequence )
    {
        if( packet.isFirst() )
        {
            insert_point = ordered_packets.begin();
        }

        ordered_packets.insert(insert_point, packet.payload());
    }

    std::vector<unsigned char> payload;

    for each( const std::vector<unsigned char> &v in ordered_packets )
    {
        payload.insert(payload.end(), v.begin(), v.end());
    }

    return payload;
}


bool TransportLayer::isTransactionComplete( void )
{
    return _ioState == Complete || _ioState == Failed || _ioState == Uninitialized;
}


bool TransportLayer::errorCondition( void )
{
    return _ioState == Failed;
}


int TransportLayer::getInputSize( void )
{
    return _payload_in.used;
}

void TransportLayer::setIoStateComplete()
{
    _ioState = Complete;
    _datalink.setIoStateComplete();
}

}
}
}

