#include "precompiled.h"

#include "encryption_oneway.h"
#include "encryption_cbcrbt.h"
#include "encryption_cmac.h"

#include "CtiTime.h"
#include "CtiDate.h"

#include <openssl/aes.h>
#include <openssl/md5.h>

#include <algorithm>



const unsigned long OneWayEncryption::_utcJan2000Seconds = CtiTime(CtiDate(1, 1, 2000), 0, 0, 0).seconds()
                                                         - CtiTime(CtiDate(1, 1, 2000), 0, 0, 0).secondOffsetToGMT();

const OneWayEncryption::Key OneWayEncryption::_yukonEncryptionKey =
{
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
};


OneWayEncryption::OneWayEncryption( const unsigned char * counter, const unsigned char * parentKey )
{
    coreKeyGen( parentKey, counter, 0x01, _encryptKey );
    coreKeyGen( parentKey, counter, 0x02, _authKey );
}


OneWayEncryption::OneWayEncryption( const unsigned char * parentKey )
{
    const unsigned char counter[] =
    {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    coreKeyGen( parentKey, counter, 0x01, _encryptKey );
    coreKeyGen( parentKey, counter, 0x02, _authKey );
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


void OneWayEncryption::coreKeyGen( const unsigned char * parentKey,
                                   const unsigned char * counter,
                                   const unsigned char   seedVal,
                                   Key                 & generatedKey )
{
    AES_KEY key;
    AES_set_encrypt_key( parentKey, key_size * 8, &key );

    Key iv, seed;

    std::copy( counter, counter + key_size, iv  );
    for ( int i = 0; i < key_size; i++ )
    {   
        seed[i] = iv[i] ^ seedVal; 
    }

    AES_cbc_encrypt( seed, generatedKey, key_size, &key, iv, AES_ENCRYPT);
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

