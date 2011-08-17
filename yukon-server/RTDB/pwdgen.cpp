#include "precompiled.h"

#include "encryption_cbcrbt.h"
#include "encryption_cmac.h"
#include "encryption_oneway.h"

#include <cstdio>
#include <cstdlib>
#include <cctype>

#include <string>
#include <algorithm>
#include <functional>

#include <openssl/md5.h>



int main (int argc, char *argv[])
{
    if ( argc != 5 || std::string( argv[1] ) != "--key" || std::string( argv[3] ) != "--password" )
    {
        std::printf("Usage: pwdgen --key 00000000000000000000000000000000 --password password\n");
        return 1;
    }

    // massage the key into form

    std::string key( std::string(32, '0') + argv[2] );

    // remove all non hex chars

    key.erase( std::remove_if( key.begin(), key.end(),
                               std::not1( std::pointer_to_unary_function<int,int>( std::isxdigit ) ) ),
               key.end() );

    // fix at 32 characters

    key = key.substr( key.length() - 32 );

    // convert

    union
    {
        unsigned long   value;
        unsigned char   bytes[ 4 ];
    }
    parser;

    unsigned char keyArray[16];

    for ( int i = 0 ; i < 4 ; ++i )
    {
        std::string subString = key.substr( i * 8, 8 );

        parser.value = std::strtoul( subString.c_str(), 0, 16 );

        std::reverse_copy( parser.bytes, parser.bytes + 4, keyArray + 4 * i );
    }

    // massage the password into form

    std::string password( argv[4] );

    std::string trimmedPassword = password.substr(0, 32);

    unsigned char IV[16];

    MD5( reinterpret_cast<const unsigned char *>( trimmedPassword.c_str() ), trimmedPassword.length(), IV );

    // generated encrypted key

    unsigned char cipherKeyArray[16];

    CbcRbtEncryption    encryptor( OneWayEncryption::_yukonEncryptionKey , IV );

    encryptor.encrypt( keyArray, 16, cipherKeyArray );

    // generate cmac

    unsigned char cmac[16];

    CmacAuthentication  cmacCalculator( OneWayEncryption::_yukonEncryptionKey );

    cmacCalculator.calculate( cipherKeyArray, 16, cmac );

    // print results

    std::printf("\nKey\t\t: ");
    for ( int i = 0 ; i < 16 ; i++ )
    {
        std::printf("%02x", keyArray[i] );
    }
    std::printf("\nPassword\t: %s\n----\n", password.c_str() );

    std::printf("Result\t\t: ");
    for ( int i = 0 ; i < 16 ; i++ )
    {
        std::printf("%02x", cipherKeyArray[i] );
    }
    for ( int i = 0 ; i < 4 ; i++ )
    {
        std::printf("%02x", cmac[i] );
    }
    std::printf("\n\n");

    return 0;
}

