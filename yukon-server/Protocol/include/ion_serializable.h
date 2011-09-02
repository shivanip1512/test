#pragma once

#include <vector>
using std::vector;

#include "dlldefs.h"


class CtiIONSerializable
{
protected:

    friend class CtiIONDatastream;
    friend class CtiIONApplicationLayer;
    friend class CtiIONNetworkLayer;
    friend class CtiIONDatalinkLayer;
    friend class CtiProtocolION;

    virtual void putSerialized( unsigned char *buf ) const = 0;
    virtual unsigned int getSerializedLength( void ) const = 0;

    unsigned char make_byte( unsigned char high_nibble, unsigned char low_nibble ) const
    {
        return (((high_nibble & 0x0f) << 4) | (low_nibble & 0x0f));
    }

public:

    CtiIONSerializable( )  { };
    virtual ~CtiIONSerializable( )  { };
};
