#include "precompiled.h"
#include "encryption.h"
#include "shlwapi.h"
#include "string_util.h"
#include "mutex.h"
#include "guard.h"

#include <openssl/aes.h>
#include <openssl/evp.h>
#include <openssl/hmac.h>
#include <openssl/err.h>

#include <cstdio>
#include <map>



namespace Cti
{
namespace Encryption
{
/*
    The following namespace is unnamed.  This is intentional.  Everything contained within it
        has its visibility reduced to this source file only.
*/
namespace
{
    typedef std::map< EncryptionType, Buffer >    FileDataMap;

    static std::string  _yukonBase;
    static FileDataMap  _fileData;

    static bool _unitTestBypass = false;

    Buffer getFileData(const EncryptionType type, const char * filename)
    {
        // return mock data if it exists -- use for unit testing

        FileDataMap::const_iterator item = _fileData.find(type);

        if ( item != _fileData.end() )
        {
            return item->second;
        }

        // no mock data supplied -- read actual file and return the buffer

        static const int ENCRYPTED_FILE_MAX_BYTES = 512;

        char    keyFile[MAX_PATH];
        PathCombine( keyFile, _yukonBase.c_str(), filename );

        FILE * fp;
        if ( fopen_s( &fp, keyFile, "rb" ) )   // Error opening
        {
            throw Error( "Could not open file: " + std::string( keyFile ) );
        }

        Buffer  wholeFile( ENCRYPTED_FILE_MAX_BYTES, 0 );
        int bytesRead = fread_s( &wholeFile[ 0 ], ENCRYPTED_FILE_MAX_BYTES, 1, ENCRYPTED_FILE_MAX_BYTES, fp );
        fclose( fp );

        if ( bytesRead && bytesRead < ENCRYPTED_FILE_MAX_BYTES )
        {
            wholeFile.resize( bytesRead );
        }
        else
        {
            throw Error( "File " + std::string( keyFile ) + " is either too large or too small" );
        }

        return wholeFile;
    }


    void generateThreeAES128Keys( const std::string & password,
                                  Buffer & aesKey, Buffer & initVector, Buffer & hmacKey )
    {
        if ( password.length() <= 0 )
        {
            throw Error( "Cannot generate keys based on empty password" );
        }

        static const int KEY_LENGTH = 16;
        static const unsigned char staticSalt[KEY_LENGTH] =
        {
            0x9B, 0x02, 0xF9, 0x92, 0x64, 0xE5, 0xE3, 0x03,
            0xF2, 0x9B, 0x19, 0x12, 0x56, 0x35, 0x56, 0x93
        };

        unsigned char saltResult[KEY_LENGTH] = { 0 };

        aesKey.clear();
        aesKey.resize( KEY_LENGTH, 0 );
        initVector.clear();
        initVector.resize( KEY_LENGTH, 0 );
        hmacKey.clear();
        hmacKey.resize( KEY_LENGTH, 0 );

        PKCS5_PBKDF2_HMAC_SHA1( password.c_str(), password.length(), staticSalt, KEY_LENGTH,  2005, KEY_LENGTH, saltResult );
        PKCS5_PBKDF2_HMAC_SHA1( password.c_str(), password.length(), saltResult, KEY_LENGTH, 10098, KEY_LENGTH, &aesKey[ 0 ] );
        PKCS5_PBKDF2_HMAC_SHA1( password.c_str(), password.length(), saltResult, KEY_LENGTH,  4019, KEY_LENGTH, &initVector[ 0 ] );
        PKCS5_PBKDF2_HMAC_SHA1( password.c_str(), password.length(), saltResult, KEY_LENGTH,  7003, KEY_LENGTH, &hmacKey[ 0 ] );
    }


    Buffer aesCbc128HmacDecryptAndHashCheck( const Buffer & cipherText,
                                             const Buffer & aesKey, const Buffer & initVector, const Buffer & hmacKey )
    {
        int updateLen   = 0,
           finalLen    = 0;

        Buffer  plainText( cipherText.size(), 0 );

        EVP_CIPHER_CTX *context = EVP_CIPHER_CTX_new();
        EVP_DecryptInit_ex( context, EVP_aes_128_cbc(), NULL, &aesKey[ 0 ], &initVector[ 0 ] );
        EVP_DecryptUpdate( context, &plainText[ 0 ], &updateLen, &cipherText[ 0 ], cipherText.size() );
        EVP_DecryptFinal_ex( context, &plainText[ updateLen ], &finalLen );
        EVP_CIPHER_CTX_free( context );

        plainText.resize( updateLen + finalLen );

        static const int HMAC_LENGTH = 32;

        if ( plainText.size() <= HMAC_LENGTH )
        {
            throw Error( "Source was not long enough to contain a valid HMAC" );
        }

        Buffer  hmac( HMAC_LENGTH, 0 );
        HMAC( EVP_sha256(), &hmacKey[ 0 ], hmacKey.size(), &plainText[ 0 ], plainText.size() - HMAC_LENGTH, &hmac[ 0 ], NULL );

        if ( memcmp( &hmac[ 0 ], &plainText[ plainText.size() - HMAC_LENGTH ], HMAC_LENGTH ) )  // No match
        {
            throw Error( "HMAC check was not successful" );
        }

        plainText.resize( plainText.size() - HMAC_LENGTH );

        // Chop off first 15 bytes of random padding -- error if not enough data

        static const int RANDOM_PAD_LENGTH = 15;

        if ( plainText.size() <= RANDOM_PAD_LENGTH )
        {
            throw Error( "Source was not long enough to contain a valid data" );
        }

        return Buffer( plainText.begin() + RANDOM_PAD_LENGTH, plainText.end() );
    }


    std::string getPasswordFromXml( const Buffer & xmlData )
    {
        std::string strData;

        strData.assign( xmlData.begin(), xmlData.end() );
        strData = matchRegex(strData, "<pk>.*</pk>");

        if ( strData.length() <= 9 )
        {
            throw Error( "Unable to find password in file" );
        }

        return strData.substr( 4, strData.length() - 9 );
    }


    void loadKeysFromFileData( const std::string & initPassword, const Buffer & fileData,
                               Buffer & aesKey, Buffer & initVector, Buffer & hmacKey )
    {
        generateThreeAES128Keys( initPassword, aesKey, initVector, hmacKey );
        Buffer  decryptedData = aesCbc128HmacDecryptAndHashCheck( fileData, aesKey, initVector, hmacKey );
        generateThreeAES128Keys( getPasswordFromXml( decryptedData ), aesKey, initVector, hmacKey );
    }


    Buffer doMasterCfgDecrypt( const Buffer & input )
    {
        static Buffer   aesKey;
        static Buffer   initVector;
        static Buffer   hmacKey;

        static CtiMutex _mutex;
        static bool     _keysLoaded = false;

        if ( ! _keysLoaded || _unitTestBypass )
        {
            CtiLockGuard< CtiMutex > guard( _mutex );
            if ( ! _keysLoaded || _unitTestBypass )
            {
                loadKeysFromFileData( "Bdk=5ohaIc51ifstd-zl2dCV)5iUE(DG",
                                      getFileData( MasterCfg, "server\\config\\keys\\masterConfigKeyfile.dat" ),
                                      aesKey, initVector, hmacKey );
                _keysLoaded = ! _unitTestBypass;
            }
        }

        return aesCbc128HmacDecryptAndHashCheck( input, aesKey, initVector, hmacKey );
    }


    Buffer doSharedKeyfileDecrypt( const Buffer & input )
    {
        static Buffer   aesKey;
        static Buffer   initVector;
        static Buffer   hmacKey;

        static CtiMutex _mutex;
        static bool     _keysLoaded = false;

        if ( ! _keysLoaded )
        {
            CtiLockGuard< CtiMutex > guard( _mutex );
            if ( ! _keysLoaded )
            {
                loadKeysFromFileData( "Bdk=5ohaIc51ifstd-zl2dCV)5iUE(DG",
                                      getFileData( SharedKeyfile, "server\\config\\keys\\sharedKeyfile.dat" ),
                                      aesKey, initVector, hmacKey );
                _keysLoaded = true;
            }
        }

        return aesCbc128HmacDecryptAndHashCheck( input, aesKey, initVector, hmacKey );
    }

}   // end -- unnamed namespace


IM_EX_CTIBASE void initialize( const std::string & yukonBase )
{
    _yukonBase = yukonBase;
    ERR_load_crypto_strings();
}


// Cleanup per https://wiki.openssl.org/index.php/Library_Initialization#Cleanup
// This overwrites memory and frees it.  
IM_EX_CTIBASE void cleanup()
{
    FIPS_mode_set(0);
    EVP_cleanup();
    CRYPTO_cleanup_all_ex_data();
    ERR_remove_thread_state(NULL);
    ERR_free_strings();
}

IM_EX_CTIBASE Buffer decrypt( const EncryptionType type, const Buffer & input )
{
    if ( type == MasterCfg )
    {
        return doMasterCfgDecrypt( input );
    }
    else if ( type == SharedKeyfile )
    {
        return doSharedKeyfileDecrypt( input );
    }

    return Buffer();
}


IM_EX_CTIBASE Buffer encrypt( const EncryptionType type, const Buffer & plainText )
{
    return Buffer();
}


IM_EX_CTIBASE void seedFileData( const EncryptionType type, const Buffer & fileData )
{
    _unitTestBypass = true;
    _fileData[ type ] = fileData;
}


IM_EX_CTIBASE void unseedFileData( const EncryptionType type )
{
    _unitTestBypass = false;
    _fileData.erase( type );
}


}
}

