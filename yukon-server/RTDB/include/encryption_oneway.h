#pragma once

#include "dlldefs.h"

#include <cstddef>


class CtiTime;



class IM_EX_DEVDB OneWayEncryption
{
public:

    static const std::size_t    key_size = 16;
    typedef unsigned char       Key[key_size];

    OneWayEncryption( const unsigned long counter, const unsigned char * parentKey );

    void encrypt( const CtiTime       & msgTime,
                  const int             sequenceNum,
                  const unsigned char * plainText,
                  const std::size_t     plainTextLen,
                  unsigned char       * cipherText );

    bool decrypt( const unsigned char * cipherText,
                  const std::size_t     cipherTextLen,
                  unsigned char       * plainText );

    static const Key _yukonEncryptionKey;

protected:

    void fillSequenceBytes( const CtiTime & msgTime, const int msgCounter, unsigned char * sequenceNum );

    void ivGen( const unsigned char * sequenceNum, const std::size_t length, Key & iv );

    Key _encryptKey;
    Key _authKey;

    static const unsigned long _utcJan2000Seconds;
};

