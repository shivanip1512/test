/*-----------------------------------------------------------------------------*
 *
 * File:   ion_serializable.h
 *
 * Class:  CtiIONSerializable
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_SERIALIZABLE_H__
#define __ION_SERIALIZABLE_H__
#pragma warning( disable : 4786 )


#include <vector>
using namespace std;

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
    ~CtiIONSerializable( )  { };
};


#endif  //  #ifndef __ION_SERIALIZABLE_H__

