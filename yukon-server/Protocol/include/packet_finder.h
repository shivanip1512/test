#pragma once

#include <vector>

#include "dlldefs.h"

namespace Cti       {
namespace Protocols {

class IM_EX_PROT PacketFinder
{
private:

    typedef std::vector<unsigned char> bytes;

    bytes _stream;

    typedef bool (*validator)(const unsigned char *, const size_t);

    validator _packet_validator;

    const unsigned char _framing0, _framing1;

    unsigned char _prev;

    std::vector<size_t> _possible_packets;

protected:

    PacketFinder(unsigned char framing0, unsigned char framing1, validator p) :
        _prev(0),
        _packet_validator(p),
        _framing0(framing0),
        _framing1(framing1)
    {
    }

public:

    typedef bytes::const_iterator const_iterator;

    //  returns true when a valid packet has been ingested
    template <class Byte>
    bool operator()(const Byte b)
    {
        unsigned char byte = b;

        if( _prev == _framing0 && byte == _framing1 )
        {
            _possible_packets.push_back(_stream.size() - 1);
        }

        _stream.push_back(byte);

        _prev = byte;

        for each( size_t offset in _possible_packets )
        {
            if( _packet_validator(&_stream.front() + offset, _stream.size() - offset) )
            {
                if( offset > 0 )
                {
                    _stream.erase(_stream.begin(),
                                  _stream.begin() + offset);
                }

                return true;
            }
        }

        return false;
    }

    const_iterator begin() const  { return _stream.begin(); };
    const_iterator end()   const  { return _stream.end();   };
    size_t         size()  const  { return _stream.size();  };
};

}
}

