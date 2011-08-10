#pragma once

#include "dlldefs.h"

#include <cstddef>



class IM_EX_DEVDB CmacAuthentication
{
public:

    static const std::size_t    key_size = 16;
    typedef unsigned char       Key[key_size];

    CmacAuthentication( const unsigned char * key );

    void calculate( const unsigned char * pInBytes, 
                    const std::size_t     nBytes,
                    Key                 & pOutBytes );

protected:

    void cmacRootKeyGen( const Key & pK, Key & root );

    void cmacKeyGen( const Key & root, Key & cmac );

    bool leftShiftKey( Key & key, const std::size_t length );

    Key _authKey;
    Key _authDKey1;
    Key _authDKey2;
};

