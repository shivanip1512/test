#include "precompiled.h"

#include "encryption_cmac.h"

#include <openssl/aes.h>

#include <algorithm>



CmacAuthentication::CmacAuthentication( const unsigned char * key )
{
    std::copy( key, key + 16, _authKey );

    Key cmacRoot;

    cmacRootKeyGen( _authKey, cmacRoot );
    cmacKeyGen( cmacRoot,   _authDKey1 );
    cmacKeyGen( _authDKey1, _authDKey2 );
}


void CmacAuthentication::cmacRootKeyGen( const Key & pK, Key & root )
{
    AES_KEY key;
    AES_set_encrypt_key( pK, key_size * 8, &key );    

    Key zeros;
    std::fill( zeros, zeros + key_size, 0 );

    AES_encrypt( zeros, root, &key );
}


void CmacAuthentication::cmacKeyGen( const Key & root, Key & cmac )
{
    std::copy( root, root + key_size, cmac );

    if ( leftShiftKey( cmac, key_size ) )
    {
        cmac[ key_size - 1 ] ^= 0x87;
    }
}


bool CmacAuthentication::leftShiftKey( Key & key, const std::size_t length )
{
    bool carryOut = key[0] & 0x80;

    unsigned char carryIn;

    for ( std::size_t i = 0 ; i < length - 1 ; ++i )
    {
        carryIn = key[ i + 1 ] & 0x80 ? 0x01 : 0x00 ;
        key[i] <<= 1;
        key[i] |= carryIn;
    }
    key[length - 1] <<= 1;

    return carryOut;
}


void CmacAuthentication::calculate( const unsigned char * pInBytes, 
                                    const std::size_t     nBytes,
                                    Key                 & pOutBytes)
{
    AES_KEY key;
    AES_set_encrypt_key( _authKey, 128, &key );    

    Key iv;
    std::fill( iv, iv + key_size, 0 );

    std::size_t bytesRemaining = nBytes ? ( ( nBytes % 16 ) ? nBytes % 16 : 16 ) : 0 ;

    for ( std::size_t i = nBytes; i > bytesRemaining; i -= 16, pInBytes += 16 )    // blocks 1 to n-1
    {
        AES_cbc_encrypt( pInBytes, pOutBytes, 16, &key, iv, AES_ENCRYPT);
    }

    Key KPrime;

    const unsigned char * useD = ( bytesRemaining == 16 ) ? _authDKey1 : _authDKey2;     // block n - full? use pK1 else use pK2

    std::copy( useD, useD + key_size, KPrime );

    for ( int i = 0; i < bytesRemaining; i++ )
    {
        KPrime[i] ^= pInBytes[i];
    }

    if ( bytesRemaining != 16 ) // partial block
    {
        KPrime[bytesRemaining] ^= 0x80;
    }

    AES_cbc_encrypt( KPrime, pOutBytes, 16, &key, iv, AES_ENCRYPT);
}

