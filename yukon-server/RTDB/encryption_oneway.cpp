#include "precompiled.h"

#include "encryption_oneway.h"
#include "encryption_cbcrbt.h"
#include "encryption_cmac.h"

#include "CtiTime.h"
#include "CtiDate.h"

#include <openssl/aes.h>
#include <openssl/md5.h>

#include <algorithm>



const unsigned long OneWayEncryption::_utcJan2000Seconds = CtiTime(CtiDate(1, 1, 2000), 0, 0, 0).getLocalTimeSeconds();


/* 
    _yukonEncryptionKey = MD5( "honorificabilitudinitatibus" );                                                                      ;
*/
const OneWayEncryption::Key OneWayEncryption::_yukonEncryptionKey =
{
    0x51, 0x0c, 0xe0, 0xdf, 0x6a, 0x07, 0x10, 0xe4,
    0xe6, 0x9c, 0xf9, 0x70, 0x34, 0x34, 0x9f, 0x3d
};


OneWayEncryption::OneWayEncryption( const unsigned long counter, const unsigned char * parentKey )
{
    unsigned char seedEncryptKey[] =
    {
        0x0c, 0x0a, 0x03, 0x06, 0x05, 0x08, 0x0c, 0x02,
        0x08, 0x0b, 0x04, 0x0a, 0x08, 0x0d, 0x02, 0x08
    };

    unsigned char seedAuthKey[] =
    {
        0xe0, 0x60, 0xf0, 0x60, 0x30, 0x90, 0xa0, 0x80,
        0xe0, 0xd0, 0x80, 0x40, 0x60, 0x10, 0x40, 0x70
    };

    unsigned char seedCounter[16];

//    if counter = 0xdeadbeef we want
//    seedCounter =
//    {
//        ~0xde, ~0xad, ~0xbe, ~0xef,  0xde,  0xad,  0xbe,  0xef,
//        ~0xde, ~0xad, ~0xbe, ~0xef,  0xde,  0xad,  0xbe,  0xef,
//    }

    for ( int i = 0; i < 4; ++i )
    {
        unsigned char byte = ( counter >> ( 24 - ( 8 * i ) ) ) & 0xff;
    
        seedCounter[ i ]     = seedCounter[ i + 8 ]  = ~byte;
        seedCounter[ i + 4 ] = seedCounter[ i + 12 ] =  byte;
    }

    for ( int i = 0; i < 16; ++i )
    {
        seedEncryptKey[ i ] ^=  seedCounter[ i ];
        seedAuthKey[ i ]    ^= ~seedCounter[ i ];
    }

    AES_KEY key;
    AES_set_encrypt_key( parentKey, key_size * 8, &key );

    AES_encrypt( seedEncryptKey, _encryptKey, &key );
    AES_encrypt( seedAuthKey, _authKey, &key );
}


void OneWayEncryption::encrypt( const CtiTime       & msgTime,
                                const int             msgCounter,
                                const unsigned char * plainText,
                                const std::size_t     plainTextLen,
                                unsigned char       * cipherText )
{
    fillSequenceBytes( msgTime, msgCounter, cipherText + plainTextLen );

    Key iv;

    ivGen( cipherText + plainTextLen, 6, iv );

    CbcRbtEncryption    encryptor( _encryptKey, iv );

    encryptor.encrypt( plainText, plainTextLen, cipherText );

    Key cmac;

    CmacAuthentication    cmacCalculator( _authKey );

    cmacCalculator.calculate( cipherText, plainTextLen + 6, cmac );

    std::copy( cmac, cmac + 4, cipherText + plainTextLen + 6 );
}


bool OneWayEncryption::decrypt( const unsigned char * cipherText,
                                const std::size_t     cipherTextLen,
                                unsigned char       * plainText )
{
    Key cmac;

    CmacAuthentication    cmacCalculator( _authKey );

    cmacCalculator.calculate( cipherText, cipherTextLen - 4, cmac );

    bool authenticated = std::equal( cipherText + cipherTextLen - 4, cipherText + cipherTextLen, cmac );

    Key iv;

    ivGen( cipherText + cipherTextLen - 10, 6, iv );

    CbcRbtEncryption    decryptor( _encryptKey, iv );

    decryptor.decrypt( cipherText, cipherTextLen - 10, plainText );

    return authenticated;
}


void OneWayEncryption::fillSequenceBytes( const CtiTime & msgTime, const int msgCounter, unsigned char * sequenceNum )
{
    const unsigned int nowSecondsForEncryption = msgTime.seconds() - _utcJan2000Seconds;

    sequenceNum[0] = ( nowSecondsForEncryption >> 24 ) & 0xff;
    sequenceNum[1] = ( nowSecondsForEncryption >> 16 ) & 0xff;
    sequenceNum[2] = ( nowSecondsForEncryption >>  8 ) & 0xff;
    sequenceNum[3] = ( nowSecondsForEncryption >>  0 ) & 0xff;
    sequenceNum[4] = ( msgCounter >>  8 ) & 0xff;
    sequenceNum[5] = ( msgCounter >>  0 ) & 0xff;
}


void OneWayEncryption::ivGen( const unsigned char * sequenceNum, const std::size_t length, Key & iv )
{
    MD5( sequenceNum, length, iv );
}

