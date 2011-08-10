#pragma once

#include "dlldefs.h"

#include <cstddef>



class IM_EX_DEVDB CbcRbtEncryption
{
public:

    static const std::size_t    key_size = 16;
    typedef unsigned char       Key[key_size];

    CbcRbtEncryption( const unsigned char * encryptKey, const unsigned char * iv );

    void encrypt( const unsigned char * pInBytes, 
                  const std::size_t     nInBytes,
                  unsigned char       * pOutBytes );

    void decrypt( const unsigned char * pInBytes, 
                  const std::size_t     nInBytes,
                  unsigned char       * pOutBytes );

protected:

    Key _encryptKey;
    Key _iv;
};

