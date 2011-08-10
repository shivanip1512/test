#include "precompiled.h"

#include "encryption_cbcrbt.h"

#include <openssl/aes.h>

#include <algorithm>



CbcRbtEncryption::CbcRbtEncryption( const unsigned char * encryptKey, const unsigned char * iv )
{
    std::copy( encryptKey, encryptKey + 16, _encryptKey );
    std::copy( iv, iv + 16, _iv );
}


void CbcRbtEncryption::encrypt( const unsigned char * pInBytes, 
                                const std::size_t     nInBytes,
                                unsigned char       * pOutBytes )
{
    AES_KEY key;
    AES_set_encrypt_key( _encryptKey, key_size * 8, &key );

    Key iv;
    std::copy( _iv, _iv + key_size, iv  );

    const std::size_t completeBlockBytes = nInBytes & ~0x0f;

    if ( completeBlockBytes > 0 )
    {
        AES_cbc_encrypt( pInBytes, pOutBytes, completeBlockBytes, &key, iv, AES_ENCRYPT);
    }

    if ( nInBytes != completeBlockBytes )
    {
        pOutBytes += completeBlockBytes;
        pInBytes  += completeBlockBytes;

        int dummy = 0;  

        AES_cfb128_encrypt( pInBytes, pOutBytes, nInBytes - completeBlockBytes, &key, iv, &dummy, AES_ENCRYPT);
    }
}


void CbcRbtEncryption::decrypt( const unsigned char * pInBytes, 
                                const std::size_t     nInBytes,
                                unsigned char       * pOutBytes )
{
    AES_KEY key;
    AES_set_decrypt_key( _encryptKey, key_size * 8, &key );

    Key iv;
    std::copy( _iv, _iv + key_size, iv  );

    const std::size_t completeBlockBytes = nInBytes & ~0x0f;

    if ( completeBlockBytes > 0 )
    {
        AES_cbc_encrypt( pInBytes, pOutBytes, completeBlockBytes, &key, iv, AES_DECRYPT);
    }

    if ( nInBytes != completeBlockBytes )
    {
        pOutBytes += completeBlockBytes;
        pInBytes  += completeBlockBytes;

        int dummy = 0;  

        AES_set_encrypt_key( _encryptKey, key_size * 8, &key );

        AES_cfb128_encrypt( pInBytes, pOutBytes, nInBytes - completeBlockBytes, &key, iv, &dummy, AES_DECRYPT);
    }
}

