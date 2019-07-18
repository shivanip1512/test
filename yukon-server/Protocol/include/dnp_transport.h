#pragma once

#include "dnp_datalink.h"
#include "xfer.h"

#include <set>

namespace Cti::Protocols::DNP {

namespace Transport {

    class TransportPacket;
}

class TransportLayer
{
protected:

    typedef std::set<Transport::TransportPacket> packet_sequence_t;

    static IM_EX_PROT bool isPacketSequenceValid(const packet_sequence_t &packet_sequence);

    static IM_EX_PROT std::vector<unsigned char> extractPayload(const packet_sequence_t &packet_sequence);

private:

    struct payload_t
    {
        unsigned char *data;
        unsigned length;
        unsigned used;
    };

    payload_t _payload_in;
    payload_t _payload_out;

    unsigned int    _current_payload_length, _sequence_in, _sequence_out;

    enum IOState
    {
        Uninitialized = 0,

        //  DNP loopback (short-circuit to Datalink layer)
        Loopback,

        Output,
        Input,
        Complete,
        Failed
    } _ioState;

    packet_sequence_t _inbound_packets;

public:
    TransportLayer();

    TransportLayer( const TransportLayer &aRef );

    TransportLayer &operator=( const TransportLayer &aRef );

    YukonError_t initLoopback();

    YukonError_t initForOutput( unsigned char *buf, unsigned len );
    YukonError_t initForInput ( unsigned char *buf, unsigned len );

    YukonError_t generate( DatalinkLayer &datalink );
    YukonError_t decode  ( DatalinkLayer &datalink );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    int  getInputSize( void );

    void setIoStateComplete();
};


namespace Transport {

class TransportPacket
{
    bool _first;
    bool _final;
    unsigned _sequence : 6;

    std::vector<unsigned char> _payload;

public:
    enum
    {
        MaxPayloadLen = 249,
        MaxPackets = 64  //  2^6 sequences = 64 packets max
    };

    TransportPacket(bool first, const unsigned seq, const unsigned char *buf, const unsigned len) :
        _first(first),
        _final(len <= MaxPayloadLen),  //  this is only the final packet if we can fit all remaining data into it
        _sequence(seq),
        _payload(buf, buf + std::min<unsigned>(len, MaxPayloadLen))
    {
    }

    template<class InputIterator>
    TransportPacket(const unsigned char header, InputIterator itr, InputIterator end) :
        _first(header & 0x40),
        _final(header & 0x80),
        _sequence(header & 0x3f),
        _payload(itr, itr + std::min<unsigned>(end - itr, MaxPayloadLen))
    {
    }

    std::vector<unsigned char> payload() const  {  return _payload;  }
    unsigned payloadLength() const  {  return _payload.size();  }

    unsigned sequence() const {  return _sequence;  }

    bool isFirst() const  {  return _first;  }
    bool isFinal() const  {  return _final;  }

    bool operator<(const TransportPacket &other) const
    {
        return _sequence < other._sequence;
    }

    operator std::vector<unsigned char>() const
    {
        std::vector<unsigned char> serialized;

        const unsigned char header = _final << 7 |
                                     _first << 6 |
                                     _sequence;

        serialized.push_back(header);

        serialized.insert(serialized.end(), _payload.begin(), _payload.end());

        return serialized;
    }

    enum
    {
        HeaderLen = 1
    };
};

}
}
